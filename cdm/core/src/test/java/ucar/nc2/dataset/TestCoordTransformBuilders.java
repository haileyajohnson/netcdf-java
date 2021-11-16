/*
 * Copyright (c) 1998-2020 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */
package ucar.nc2.dataset;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.unidata.util.test.TestDir;
import ucar.unidata.util.test.category.NeedsCdmUnitTest;

/** Test CoordTransformBuilders */
@Category(NeedsCdmUnitTest.class)
@RunWith(Parameterized.class)
public class TestCoordTransformBuilders {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static String convDir = TestDir.cdmUnitTestDir + "/conventions";
  private static List<String> otherDirs =
      ImmutableList.of(TestDir.cdmUnitTestDir + "/ft", TestDir.cdmUnitTestDir + "/cfPoint");

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> getTestParameters() {
    Collection<Object[]> filenames = new ArrayList<>();
    try {
      TestDir.actOnAllParameterized(convDir, (file) -> !file.getPath().endsWith(".pdf"), filenames, true);
      for (String dir : otherDirs) {
        TestDir.actOnAllParameterized(dir, (file) -> file.getPath().endsWith(".nc"), filenames, true);
      }
    } catch (IOException e) {
      filenames.add(new Object[] {e.getMessage()});
    }
    return filenames;
  }

  private String fileLocation;

  public TestCoordTransformBuilders(String filename) {
    this.fileLocation = "file:" + filename;
  }

  @Test
  public void testCoordTransforms() throws IOException {
    System.out.printf("testCoordTransforms %s%n", fileLocation);
    try (NetcdfDataset ncdb = NetcdfDatasets.openDataset(fileLocation)) {
      for (CoordinateTransform ct : ncdb.getCoordinateTransforms()) {
        System.out.printf("  %s%n", ct);
      }
    }
  }
}


