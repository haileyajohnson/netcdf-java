/*
 * Copyright (c) 1998-2018 John Caron and University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.ft.point.writer;

import com.google.common.collect.ImmutableList;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.write.Nc4Chunking;
import ucar.nc2.write.Nc4ChunkingDefault;
import ucar.nc2.write.Nc4ChunkingStrategy;

import java.util.List;

/**
 * Configuration for CFPointWriter
 *
 * @author caron
 * @since 6/23/2014
 */
public class CFPointWriterConfig {
  public final NetcdfFileWriter.Version version; // netcdf file version
  public final Nc4Chunking chunking; // for netcdf-4

//  public final ImmutableList<VariableSimpleIF> coords;

  public CFPointWriterConfig(NetcdfFileWriter.Version version) {
    this(version, new Nc4ChunkingDefault()); // The default chunker used in Nc4Iosp.
  }

  public CFPointWriterConfig(NetcdfFileWriter.Version version, Nc4Chunking chunking) {
    this.version = version;
    this.chunking = chunking;
  }

//  public CFPointWriterConfig(NetcdfFileWriter.Version version, Nc4ChunkingStrategy chunking, List<VariableSimpleIF> co)
}
