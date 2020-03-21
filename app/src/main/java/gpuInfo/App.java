package gpuInfo;
import static jcuda.runtime.JCuda.cudaGetDeviceCount;
import static jcuda.runtime.JCuda.cudaGetDeviceProperties;
import static jcuda.runtime.JCuda.cudaDriverGetVersion;
import static jcuda.runtime.JCuda.cudaRuntimeGetVersion;



import jcuda.runtime.JCuda;
import jcuda.runtime.cudaDeviceProp;
public class App 
{
    public static void main( String[] args )
    {
        JCuda.setExceptionsEnabled(true);
        int deviceCount[] = { 0 };
        int driverVersion[] = { 0 };
        int runtimeVersion[] = { 0 };
        cudaGetDeviceCount(deviceCount);
        System.out.println("Found " + deviceCount[0] + " devices");
        for (int device = 0; device < deviceCount[0]; device++)
        {
            cudaDeviceProp deviceProperties = new cudaDeviceProp();
            cudaGetDeviceProperties(deviceProperties, device);
            cudaDriverGetVersion(driverVersion);
            cudaRuntimeGetVersion(runtimeVersion);

            System.out.println("Properties of device: " + deviceProperties.getName());
            System.out.println("CUDA driver version: " + 
            driverVersion[0] / 1000 + "." + (driverVersion[0] % 100) / 10);
            System.out.println("CUDA runtime version: " + 
            runtimeVersion[0] / 1000 + "." + (runtimeVersion[0] % 100) / 10);
            System.out.println("total global memory: " + deviceProperties.totalGlobalMem / 1048576.0f + " MBytes");
            System.out.println("multiprocessors: " + deviceProperties.multiProcessorCount);
            System.out.println("CUDA Cores/MP: " + _ConvertSMVer2Cores(deviceProperties.major, deviceProperties.minor));
            System.out.println("CUDA Cores: " + _ConvertSMVer2Cores(deviceProperties.major, deviceProperties.minor) * deviceProperties.multiProcessorCount);            
        }
    }
    static int _ConvertSMVer2Cores(int major, int minor) {
        // Defines for GPU Architecture types (using the SM version to determine
        // the # of cores per SM
        class sSMtoCores 
        {

            sSMtoCores(int x, int y)
            {
                SM = x;
                Cores = y;
            }
            int SM;  // 0xMm (hexidecimal notation), M = SM Major version,
          // and m = SM minor version
            int Cores;
            
        }
        sSMtoCores nGpuArchCoresPerSM[] = {
            new sSMtoCores(0x30, 192),
            new sSMtoCores(0x32, 192),
            new sSMtoCores(0x35, 192),
            new sSMtoCores(0x37, 192),
            new sSMtoCores(0x50, 128),
            new sSMtoCores(0x52, 128),
            new sSMtoCores(0x53, 128),
            new sSMtoCores(0x60,  64),
            new sSMtoCores(0x61, 128),
            new sSMtoCores(0x62, 128),
            new sSMtoCores(0x70,  64),
            new sSMtoCores(0x72,  64),
            new sSMtoCores(0x75,  64),
            new sSMtoCores(-1, -1)};
      
        int index = 0;
      
        while (nGpuArchCoresPerSM[index].SM != -1) {
          if (nGpuArchCoresPerSM[index].SM == ((major << 4) + minor)) {
            return nGpuArchCoresPerSM[index].Cores;
          }
      
          index++;
        }
      
        // If we don't find the values, we default use the previous one
        // to run properly
        System.out.println("MapSMtoCores for SM %d.%d is undefined.");
        return nGpuArchCoresPerSM[index - 1].Cores;
      }
      
}
