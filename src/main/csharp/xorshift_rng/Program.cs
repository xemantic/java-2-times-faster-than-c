using System;
using System.Linq;

namespace XorShift
{
    static class Program
    {
        private const ulong ITERATION_COUNT = 1000000000L;

        static void Main(string[] args)
        {
            double checksum = 0;
            XorShift64SRng rng = new XorShift64SRng(42);
            for (uint i = 0; i < ITERATION_COUNT; i++)
            {
                checksum += rng.getRand();
            }

            Console.WriteLine($"checksum: {checksum}");
        }
    }
}
