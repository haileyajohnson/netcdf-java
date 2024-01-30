package ucar.nc2.ft2.coverage.writer;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.NetcdfDatasets;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.nc2.ft.FeatureDatasetPoint;
import ucar.nc2.ft.point.writer.CFPointWriter;
import ucar.nc2.util.CompareNetcdf2;

import java.io.*;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import static com.google.common.truth.Truth.*;

@RunWith(Parameterized.class)
public class TestCFPointWriter {

  private static final String testPath = "src/test/data/point/";

  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Parameterized.Parameters(name = "{0}/{1}")
  public static List<Object[]> getTestParameters() {
    return Arrays.asList(new Object[][] {
        // Point
        {FeatureType.POINT, "point.ncml"},
        // Two points features with different time dimensions
        {FeatureType.POINT, "multiPoint.ncml"},
        // Profile
        {FeatureType.PROFILE, "profileSingle.ncml"},
        // Station
        {FeatureType.STATION, "stationSingle.ncml"},
        // Station profile
        {FeatureType.STATION_PROFILE, "stationProfileSingle.ncml"},
        // Trajectory
        {FeatureType.TRAJECTORY, "trajSingle.ncml"}});
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
    File outFile = tempFolder.newFile();
    FeatureDatasetPoint fdPoint = openPointDataset(wantedType, datasetFile);
    CFPointWriter.writeFeatureCollection(fdPoint, outFile.getAbsolutePath(), NetcdfFileWriter.Version.netcdf3);
    compareNetCDF(datasetFile, outFile);
  }

  private static FeatureDatasetPoint openPointDataset(FeatureType wantedType, File datasetFile) throws IOException {
    Formatter errlog = new Formatter();
    FeatureDataset fDset = FeatureDatasetFactoryManager.open(wantedType, datasetFile.getAbsolutePath(), null, errlog);

    assert fDset != null : "No factory found: " + errlog;
    return (FeatureDatasetPoint) fDset;
  }

  private static void compareNetCDF(File expectedResultFile, File actualResultFile) throws IOException {
    try (NetcdfFile expectedNcFile = NetcdfDatasets.openDataset(expectedResultFile.getAbsolutePath());
        NetcdfFile actualNcFile = NetcdfDatasets.openDataset(actualResultFile.getAbsolutePath())) {
      assertThat(CompareNetcdf2.compareFiles(expectedNcFile, actualNcFile, new Formatter(), false, true, true));
    }
  }

}
