package ucar.nc2.ft.point;

import ucar.ma2.DataType;
import ucar.nc2.constants.CF;

public class CollectionZInfo {
  protected final String name, units, longName, positive, axis;
  protected final DataType dataType;


  private final static String DEFAULT_NAME = "altitude";
  private final static String DEFAULT_UNIT = "meters above sea level";
  private final static String DEFAULT_POSITIVE = CF.POSITIVE_UP;

  private final static DataType DEFAULT_DTYPE = DataType.DOUBLE;

  public CollectionZInfo(String name, String units, String longName, String positive, String axis, DataType dataType) {
    this.name = name != null ? name : DEFAULT_NAME;
    this.units = units != null ? units : DEFAULT_UNIT;
    this.longName = longName != null ? longName : DEFAULT_NAME;
    this.positive = positive != null ? positive : DEFAULT_POSITIVE;
    this.axis = axis;
    this.dataType = dataType != null ? dataType : DEFAULT_DTYPE;
  }

  public String getName() {
    return name;
  }

  public String getUnits() {
    return units;
  }

  public String getLongName() {
    return longName;
  }

  public String getPositive() {
    return positive;
  }

  public String getAxis() {
    return axis;
  }

  public DataType getDataType() {
    return dataType;
  }
}
