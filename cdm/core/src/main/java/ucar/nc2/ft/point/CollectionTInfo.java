package ucar.nc2.ft.point;

import ucar.nc2.time.CalendarDateUnit;

public class CollectionTInfo {
  protected final String name, longName;
  protected final CalendarDateUnit units;
  private final static String DEFAULT_NAME = "time";
  private final static CalendarDateUnit DEFAULT_UNIT = CalendarDateUnit.unixDateUnit;

  public CollectionTInfo(String name, CalendarDateUnit units, String longName) {
    this.name = name != null ? name : DEFAULT_NAME;
    this.units = units != null ? units : DEFAULT_UNIT;
    this.longName = longName != null ? longName : DEFAULT_NAME;
  }

  public String getName() {
    return name;
  }

  public CalendarDateUnit getUnits() {
    return units;
  }

  public String getLongName() {
    return longName;
  }

}
