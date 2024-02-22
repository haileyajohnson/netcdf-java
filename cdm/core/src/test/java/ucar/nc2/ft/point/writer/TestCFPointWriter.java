package ucar.nc2.ft.point.writer;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ucar.nc2.*;
import ucar.nc2.constants.CDM;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.NetcdfDatasets;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.nc2.ft.FeatureDatasetPoint;
import ucar.nc2.ft.point.writer.CFPointWriter;
import ucar.nc2.util.CompareNetcdf2;

import java.io.*;
import java.util.*;

import static com.google.common.truth.Truth.*;

@RunWith(Parameterized.class)
public class TestCFPointWriter {

  private static final String testPath = "src/test/data/cfPoints/";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Parameterized.Parameters(name = "{0}/{1}")
  public static List<Object[]> getTestParameters() {
    return Arrays.asList(new Object[][] {
        // Point
        {FeatureType.POINT, "point.ncml"},
//        // Two points features with different time dimensions
//        {FeatureType.POINT, "multiPoint.ncml"},
        // Profile
//        {FeatureType.PROFILE, "profileSingle.ncml"},
//        // Station
//        {FeatureType.STATION, "stationSingle.ncml"},
//        // Station profile
//        {FeatureType.STATION_PROFILE, "stationProfileSingle.ncml"},
//        // Trajectory
//        {FeatureType.TRAJECTORY, "trajSingle.ncml"}
        });
  }

  private final FeatureType wantedType;
  private final String datasetName;

  public TestCFPointWriter(FeatureType wantedType, String datasetName) {
    this.wantedType = wantedType;
    this.datasetName = testPath + datasetName;
  }

  @Test
  public void testWritePointFeatures() throws IOException {
    File datasetFile = new File(datasetName);
    File outFile = File.createTempFile("testfile", null);//tempFolder.newFile();
    FeatureDatasetPoint fdPoint = openPointDataset(wantedType, datasetFile);
    CFPointWriter.writeFeatureCollection(fdPoint, outFile.getAbsolutePath(), NetcdfFileWriter.Version.netcdf3);
    assertThat(compareNetCDF(datasetFile, outFile)).isTrue();
  }

  private static FeatureDatasetPoint openPointDataset(FeatureType wantedType, File datasetFile) throws IOException {
    Formatter errlog = new Formatter();
    FeatureDataset fDset = FeatureDatasetFactoryManager.open(wantedType, datasetFile.getAbsolutePath(), null, errlog);
    return (FeatureDatasetPoint) fDset;
  }

  private static boolean compareNetCDF(File expectedResultFile, File actualResultFile) throws IOException {
    try (NetcdfFile expectedNcFile = NetcdfDatasets.openDataset(expectedResultFile.getAbsolutePath());
        NetcdfFile actualNcFile = NetcdfDatasets.openDataset(actualResultFile.getAbsolutePath())) {
      Formatter formatter = new Formatter();
      CompareNetcdf2 compareNetcdf2 = new CompareNetcdf2(formatter, true, false, true, true);
      boolean contentsAreEqual = compareNetcdf2.compare(expectedNcFile, actualNcFile, new CFObjFilter());
      if (!contentsAreEqual) {
        System.err.println(formatter);
      }
      return contentsAreEqual;
    }
  }

  private static class CFObjFilter implements CompareNetcdf2.ObjFilter {
    private static Map<String, List<String>> ignore;
    static {
      ignore = new HashMap<>();
      ArrayList global = new ArrayList();
      global.add(CDM.HISTORY);
      global.add("time_coverage_start");
      global.add("time_coverage_end");
      global.add("geospatial_lat_min");
      global.add("geospatial_lon_min");
      global.add("geospatial_lat_max");
      global.add("geospatial_lon_max");
      global.add("DSG_representation");
      ignore.put("global", global);
      ArrayList time = new ArrayList();
      time.add("calendar");
      time.add("units");
      ignore.put("time", time);
    }
    @Override
    public boolean attCheckOk(Variable v, Attribute att) {
      String name = v == null ? "global" :  v.getFullName();
      return ignore.getOrDefault(name, new ArrayList<>()).stream().noneMatch(s -> s.equals(att.getShortName()));
    }
  }
}
