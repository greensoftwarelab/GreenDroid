package jInst.profiler;

/**
 * Created by rrua on 17/06/17.
 */
public interface ProfilerAbstractFactory {

   TestOrientedProfiler createTestOrientedProfiler();
   MethodOrientedProfiler createMethodOrientedProfiler();

}
