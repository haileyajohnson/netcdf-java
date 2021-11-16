package ucar.nc2.dataset.conv;

import org.junit.Assert;
import org.junit.Test;
import ucar.ma2.Array;
import ucar.nc2.Variable;
import ucar.nc2.constants.CF;
import ucar.nc2.constants._Coordinate;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.unidata.util.test.TestDir;
import java.io.IOException;
import java.util.List;
import static java.lang.String.format;

public class TestSimpleGeom {

  private final String cfConvention = CF1Convention.class.getName();

  @Test
  public void testLine() throws IOException {

    String failMessage, found, expected;
    boolean testCond;

    String tstFile = TestDir.cdmLocalTestDataDir + "dataset/SimpleGeos/hru_soil_moist_vlen_3hru_5timestep.nc";

    // open the test file
    NetcdfDataset ncd = NetcdfDataset.openDataset(tstFile);

    // make sure this dataset used the cfConvention
    expected = cfConvention;
    found = ncd.getConventionUsed();
    testCond = found.equals(expected);
    failMessage =
        format("This dataset used the %s convention, but should have used the %s convention.", found, expected);
    Assert.assertTrue(failMessage, testCond);

    // check that attributes were filled in correctly
    List<Variable> vars = ncd.getVariables();
    for (Variable v : vars) {
      if (v.findAttribute(CF.GEOMETRY) != null) {
        Assert.assertNotNull(v.findAttribute(CF.NODE_COORDINATES));
        Assert.assertNotNull(v.findAttribute(_Coordinate.Axes));
      }
    }
    ncd.close();
  }

  @Test
  public void testPolygon() throws IOException {

    String failMessage, found, expected;
    boolean testCond;

    String tstFile = TestDir.cdmLocalTestDataDir + "dataset/SimpleGeos/outflow_3seg_5timesteps_vlen.nc";

    // open the test file
    NetcdfDataset ncd = NetcdfDataset.openDataset(tstFile);

    // make sure this dataset used the cfConvention
    expected = cfConvention;
    found = ncd.getConventionUsed();
    testCond = found.equals(expected);
    failMessage =
        format("This dataset used the %s convention, but should have used the %s convention.", found, expected);
    Assert.assertTrue(failMessage, testCond);

    // check that attributes were filled in correctly
    List<Variable> vars = ncd.getVariables();
    for (Variable v : vars) {
      if (v.findAttribute(CF.GEOMETRY) != null) {
        Assert.assertNotNull(v.findAttribute(CF.NODE_COORDINATES));
        Assert.assertNotNull(v.findAttribute(_Coordinate.Axes));
      }
    }
    ncd.close();
  }

  @Test
  public void testCoordinateVariable() throws IOException {
    String tstFile = TestDir.cdmLocalTestDataDir + "dataset/SimpleGeos/outflow_3seg_5timesteps_vlen.nc";
    // open the test file
    try (NetcdfDataset ncd = NetcdfDataset.openDataset(tstFile)) {
      for (CoordinateAxis axis : ncd.getCoordinateAxes()) {
        System.out.printf("Try to read %s ", axis.getFullName());
        Array data = axis.read();
        System.out.printf(" OK (%d) %n", data.getSize());
      }
    }
  }

  @Test
  public void testVarLenDataVariable() throws IOException {
    String tstFile = TestDir.cdmLocalTestDataDir + "dataset/SimpleGeos/outflow_3seg_5timesteps_vlen.nc";
    // open the test file
    try (NetcdfDataset ncd = NetcdfDataset.openDataset(tstFile)) {
      for (CoordinateAxis axis : ncd.getCoordinateAxes()) {
        System.out.printf("Try to read %s ", axis.getFullName());
        Array data = axis.read();
        System.out.printf(" OK (%d) %n", data.getSize());
      }
    }
  }
}
