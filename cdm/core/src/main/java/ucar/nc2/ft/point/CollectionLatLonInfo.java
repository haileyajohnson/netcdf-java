package ucar.nc2.ft.point;

import ucar.ma2.DataType;

public class CollectionLatLonInfo {
  protected final String nameLat, unitsLat, longNameLat;
  protected final String nameLon, unitsLon, longNameLon;
  protected final DataType dataTypeLat, dataTypeLon;
  private static final String DEFAULT_LATITUDE_NAME = "latitude";
  private static final String DEFAULT_LATITUDE_UNIT = "degrees_north";
  private static final String DEFAULT_LONGITUDE_NAME = "longitude";
  private static final String DEFAULT_LONGITUDE_UNIT = "degrees_east";
  private static final DataType DEFAULT_DTYPE = DataType.DOUBLE;

  public CollectionLatLonInfo(String nameLat, String unitsLat, String longNameLat, DataType dataTypeLat, String nameLon,
      String unitsLon, String longNameLon, DataType dataTypeLon) {
    this.nameLat = nameLat != null ? nameLat : DEFAULT_LATITUDE_NAME;
    this.unitsLat = unitsLat != null ? unitsLat : DEFAULT_LATITUDE_UNIT;
    this.longNameLat = longNameLat != null ? longNameLat : DEFAULT_LATITUDE_NAME;
    this.dataTypeLat = dataTypeLat != null ? dataTypeLat : DEFAULT_DTYPE;
    this.nameLon = nameLon != null ? nameLon : DEFAULT_LONGITUDE_NAME;
    this.unitsLon = unitsLon != null ? unitsLon : DEFAULT_LONGITUDE_UNIT;
    this.longNameLon = longNameLon != null ? longNameLon : DEFAULT_LONGITUDE_NAME;
    this.dataTypeLon = dataTypeLon != null ? dataTypeLon : DEFAULT_DTYPE;
  }

  ////// Latitude
  public String getLatName() {
    return nameLat;
  }

  public String getLatUnits() {
    return unitsLat;
  }

  public String getLatLongName() {
    return longNameLat;
  }

  public DataType getLatDataType() {
    return dataTypeLat;
  }

  ////// Longitude

  public String getLonName() {
    return nameLon;
  }

  public String getLonUnits() {
    return unitsLon;
  }

  public String getLonLongName() {
    return longNameLon;
  }

  public DataType getLonDataType() {
    return dataTypeLon;
  }
}
