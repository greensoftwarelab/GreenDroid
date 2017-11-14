package jInst.profiler.trepn;

import jInst.profiler.MethodOrientedProfiler;
import jInst.profiler.ProfilerAbstractFactory;
import jInst.profiler.TestOrientedProfiler;

/**
 * Created by rrua on 17/06/17.
 */
public class TrepnProfilerFactory  implements ProfilerAbstractFactory {

    @Override
    public TestOrientedProfiler createTestOrientedProfiler() {
        return new TrepnTestOrientedProfiler();
    }

    @Override
    public MethodOrientedProfiler createMethodOrientedProfiler() {
        return new TrepnMethodOrientedProfiler();
    }
}
