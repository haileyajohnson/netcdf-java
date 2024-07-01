/*
 * Copyright (c) 1998-2018 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ft.point.writer;

import com.google.common.collect.ImmutableList;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants.CF;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.time.CalendarDate;
import ucar.nc2.time.CalendarDateUnit;
import ucar.nc2.*;
import ucar.nc2.ft.*;
import ucar.unidata.geoloc.EarthLocation;
import ucar.ma2.*;
import java.util.*;
import java.io.IOException;

/**
 * Write a CF 1.6 "Discrete Sample" point file.
 *
 * <pre>
 *   writeHeader()
 *   iterate { writeRecord() }
 *   finish()
 * </pre>
 *
 * @see "http://cf-pcmdi.llnl.gov/documents/cf-conventions/1.6/cf-conventions.html#idp8294224"
 * @author caron
 * @since Nov 23, 2010
 */
public class WriterCFPointCollection extends CFPointWriter {


  // TODO: should this to continue to work the old way?
  public WriterCFPointCollection(String fileOut, List<Attribute> globalAtts, List<VariableSimpleIF> dataVars,
      CalendarDateUnit timeUnit, String altUnits, CFPointWriterConfig config) throws IOException {
    this(fileOut, globalAtts, dataVars, new ArrayList<>(), config);
  }

  public WriterCFPointCollection(String fileOut, List<Attribute> globalAtts, List<VariableSimpleIF> dataVars,
            List<CoordinateAxis> coordVars, CFPointWriterConfig config) throws IOException {
    super(fileOut, globalAtts, dataVars, config, coordVars);
    writer.addGroupAttribute(null, new Attribute(CF.FEATURE_TYPE, CF.FeatureType.point.name()));
    writer.addGroupAttribute(null, new Attribute(CF.DSG_REPRESENTATION, "Point Data, H.1"));
  }

  protected void setDimensions() {
    // point collections only have one dimension, the "record dimension"
    CoordinateAxis outsideCoord = this.coordVars.get(0);
    this.outsideVarName = outsideCoord.getShortName();
    if (outsideCoord.getDimensions().isEmpty()) {
      this.outsideDim = Dimension.builder().setName(recordDimName).setIsUnlimited(true).build();
    } else {
      this.outsideDim = outsideCoord.getDimension(0);
    }
  }

  // TODO: clean up all writeHeader
  public void writeHeader(PointFeature pf) throws IOException {
    List<VariableSimpleIF> coords = new ArrayList<>();
    DsgFeatureCollection fc = pf.getFeatureCollection();
    Formatter coordNames = new Formatter().format("%s %s %s %s", fc.getTimeName(), "latitude",
        "longitude", fc.getAltName());
    super.writeHeader(coords, null, pf.getDataAll(), coordNames.toString());
  }
  public void writeHeader(PointFeatureCollection pfc) throws IOException {
    List<VariableSimpleIF> coords = new ArrayList<>();
    super.writeHeader(coords, Arrays.asList(pfc), null, null);
  }

  protected void makeFeatureVariables(List<StructureData> featureData, boolean isExtended) {
    // NOOP
  }

  /////////////////////////////////////////////////////////
  // writing data

  public void writeRecord(PointFeature sobs, StructureData sdata) throws IOException {
    writeRecord(sobs.getObservationTime(), sobs.getObservationTimeAsCalendarDate(), sobs.getLocation(), sdata);
  }

  private int obsRecno;

  public void writeRecord(double timeCoordValue, CalendarDate obsDate, EarthLocation loc, StructureData sdata)
      throws IOException {
    trackBB(loc.getLatLon(), obsDate);

    StructureMembers.Builder smb = StructureMembers.builder().setName("Coords");
    for (CoordinateAxis coord : coordVars) {
      Number val = null;
      AxisType axis = coord.getAxisType();
      if (axis.isTime()) {
        val = timeCoordValue;
      } else if (axis.isVert()) {
        val = loc.getAltitude();
      } else if (axis.isHoriz()) {
        val = axis == AxisType.Lat ? loc.getLatitude() : loc.getLongitude();
      }
      smb.addMemberScalar(coord.getShortName(), coord.getDescription(), coord.getUnitsString(), coord.getDataType(), val);
    }
    StructureData coords = new StructureDataFromMember(smb.build());

    // coords first so it takes precedence
    StructureDataComposite sdall = StructureDataComposite.create(ImmutableList.of(coords, sdata));
    obsRecno = super.writeStructureData(obsRecno, record, sdall, dataMap);
  }
}
