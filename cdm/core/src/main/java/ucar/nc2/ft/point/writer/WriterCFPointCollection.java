/*
 * Copyright (c) 1998-2018 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ft.point.writer;

import com.google.common.collect.ImmutableList;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.CF;
import ucar.nc2.dataset.conv.CF1Convention;
import ucar.nc2.ft.point.CollectionLatLonInfo;
import ucar.nc2.ft.point.CollectionTInfo;
import ucar.nc2.ft.point.CollectionZInfo;
import ucar.nc2.ft.point.DsgCollectionImpl;
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

  public WriterCFPointCollection(String fileOut, List<Attribute> globalAtts, List<VariableSimpleIF> dataVars,
      CalendarDateUnit timeUnit, String altUnits, CFPointWriterConfig config) throws IOException {
    this(fileOut, globalAtts, dataVars, new CollectionTInfo(null, timeUnit, null),
        new CollectionZInfo(null, altUnits, null, null, null, null),
        new CollectionLatLonInfo(null, null, null, null, null, null, null, null), config);
  }

  public WriterCFPointCollection(String fileOut, List<Attribute> globalAtts, List<VariableSimpleIF> dataVars,
      CollectionTInfo tInfo, CollectionZInfo zInfo, CollectionLatLonInfo latLonInfo, CFPointWriterConfig config)
      throws IOException {
    super(fileOut, globalAtts, dataVars, tInfo, zInfo, latLonInfo, config);
    writer.addGroupAttribute(null, new Attribute(CF.FEATURE_TYPE, CF.FeatureType.point.name()));
    writer.addGroupAttribute(null, new Attribute(CF.DSG_REPRESENTATION, "Point Data, H.1"));
  }

  public void writeHeader(PointFeature pf) throws IOException {
    List<VariableSimpleIF> coords = new ArrayList<>();
    DsgFeatureCollection fc = pf.getFeatureCollection();
    writeHeader(coords, fc);
    Formatter coordNames = new Formatter().format("%s %s %s %s", fc.getTimeName(), fc.getLatLonInfo().getLatName(),
        fc.getLatLonInfo().getLonName(), fc.getAltName());
    super.writeHeader(coords, null, pf.getDataAll(), coordNames.toString());
  }

  public void writeHeader(PointFeatureCollection pfc) throws IOException {
    List<VariableSimpleIF> coords = new ArrayList<>();
    writeHeader(coords, pfc);
    super.writeHeader(coords, Arrays.asList(pfc), null, null);
  }

  private void writeHeader(List<VariableSimpleIF> coords, DsgFeatureCollection fc) {
    // time info
    tInfo = fc.getTInfo();
    if (tInfo != null && tInfo.getUnits() != null) {
      coords.add(VariableSimpleBuilder
          .makeScalar(tInfo.getName(), tInfo.getLongName(), tInfo.getUnits().getUdUnit(), DataType.DOUBLE)
          .addAttribute(CF.CALENDAR, tInfo.getUnits().getCalendar().toString()).build());
    }

    // lon/lat info
    latLonInfo = fc.getLatLonInfo();
    coords.add(VariableSimpleBuilder.makeScalar(latLonInfo.getLatName(), latLonInfo.getLatLongName(),
        latLonInfo.getLatUnits(), latLonInfo.getLatDataType()).build());
    coords.add(VariableSimpleBuilder.makeScalar(latLonInfo.getLonName(), latLonInfo.getLonLongName(),
        latLonInfo.getLonUnits(), latLonInfo.getLatDataType()).build());

    // alt info
    zInfo = fc.getZInfo();
    if (zInfo != null && zInfo.getUnits() != null) {
      coords.add(
          VariableSimpleBuilder.makeScalar(zInfo.getName(), zInfo.getLongName(), zInfo.getUnits(), zInfo.getDataType())
              .addAttribute("positive", zInfo.getPositive()).addAttribute("axis", zInfo.getAxis()).build());
    }
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
    smb.addMemberScalar(tInfo.getName(), tInfo.getLongName(), tInfo.getUnits().getUdUnit(), DataType.DOUBLE,
        timeCoordValue);
    smb.addMemberScalar(latLonInfo.getLatName(), latLonInfo.getLatLongName(), latLonInfo.getLatUnits(),
        latLonInfo.getLatDataType(), loc.getLatitude());
    smb.addMemberScalar(latLonInfo.getLonName(), latLonInfo.getLonLongName(), latLonInfo.getLonUnits(),
        latLonInfo.getLonDataType(), loc.getLongitude());
    if (altUnits != null)
      smb.addMemberScalar(zInfo.getName(), zInfo.getLongName(), zInfo.getUnits(), zInfo.getDataType(),
          loc.getAltitude());
    StructureData coords = new StructureDataFromMember(smb.build());

    // coords first so it takes precedence
    StructureDataComposite sdall = StructureDataComposite.create(ImmutableList.of(coords, sdata));
    obsRecno = super.writeStructureData(obsRecno, record, sdall, dataMap);
  }
}
