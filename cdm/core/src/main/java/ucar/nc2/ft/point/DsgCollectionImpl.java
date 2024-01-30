/*
 * Copyright (c) 1998-2018 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */
package ucar.nc2.ft.point;

import org.checkerframework.checker.units.qual.C;
import ucar.ma2.DataType;
import ucar.nc2.Variable;
import ucar.nc2.constants.CF;
import ucar.nc2.ft.DsgFeatureCollection;
import ucar.nc2.time.CalendarDateRange;
import ucar.nc2.time.CalendarDateUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Common methods for DsgFeatureCollection.
 *
 * @author caron
 * @since 9/25/2015.
 */
public abstract class DsgCollectionImpl implements DsgFeatureCollection {

  protected String name;
  protected String timeName = "time";
  protected CalendarDateUnit timeUnit;
  protected String altName = "altitude";
  protected String altUnits;
  protected CollectionInfo info;
  protected CollectionZInfo zInfo;
  protected CollectionTInfo tInfo;
  protected CollectionLatLonInfo latLonInfo;
  protected List<Variable> extras; // variables needed to make CF/DSG writing work

  protected DsgCollectionImpl(String name, CalendarDateUnit timeUnit, String altUnits) {
    this(name, CF.TIME, timeUnit, null, altUnits);
  }

  protected DsgCollectionImpl(String name, String timeName, CalendarDateUnit timeUnit, String altName,
      String altUnits) {
    this(name, new CollectionTInfo(timeName, timeUnit, null),
        new CollectionZInfo(altName, altUnits, null, null, "Z", null),
        new CollectionLatLonInfo(null, null, null, null, null, null, null, null));
  }

  protected DsgCollectionImpl(String name, CollectionTInfo time, CollectionZInfo alt, CollectionLatLonInfo latLonInfo) {
    this.name = name;
    this.tInfo = time;
    this.zInfo = alt;
    this.timeName = tInfo.name;
    this.timeUnit = tInfo.units;
    this.altName = zInfo.name;
    this.altUnits = zInfo.units;
    this.latLonInfo = latLonInfo;
  }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Nonnull
  @Override
  public String getTimeName() {
    return timeName;
  }

  @Nonnull
  @Override
  public CalendarDateUnit getTimeUnit() {
    return timeUnit;
  }

  @Nullable
  @Override
  public String getAltName() {
    return altName;
  }

  @Nullable
  @Override
  public String getAltUnits() {
    return altUnits;
  }

  @Override
  public CollectionTInfo getTInfo() {
    return tInfo;
  }

  @Override
  public CollectionZInfo getZInfo() {
    return zInfo;
  }

  @Override
  public CollectionLatLonInfo getLatLonInfo() {
    return latLonInfo;
  }

  @Nonnull
  public List<Variable> getExtraVariables() {
    return (extras == null) ? new ArrayList<>() : extras;
  }

  @Override
  public int size() {
    return (info == null) ? -1 : info.nfeatures;
  }

  public int getNobs() {
    return (info == null) ? -1 : info.nobs;
  }

  @Nullable
  @Override
  public CalendarDateRange getCalendarDateRange() {
    return (info == null) ? null : info.getCalendarDateRange(timeUnit);
  }

  @Nullable
  @Override
  public ucar.unidata.geoloc.LatLonRect getBoundingBox() {
    return (info == null) ? null : info.bbox;
  }

  @Nonnull
  public CollectionInfo getInfo() { // LOOK exposes mutable fields
    if (info == null)
      info = new CollectionInfo();
    return info;
  }
}
