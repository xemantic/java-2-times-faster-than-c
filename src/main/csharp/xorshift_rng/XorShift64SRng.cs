using System;
using System.Linq;

namespace XorShift
{
    public class XorShift64SRng
    {
        private ulong State;

        public XorShift64SRng(ulong state)
        {
            State = state;
        }

        public double getRand()
        {
            ulong x = State;
            x ^= x >> 12; // a
            x ^= x << 25; // b
            x ^= x >> 27; // c
            State = x;

            ulong rand_val = x * 0x2545F4914F6CDD1DL;

            // mix to a double
            uint a = (uint) (rand_val >> 32);
            uint b = (uint) rand_val;

            return ((double) (a >> 5) * 67108864.0 + (double) (b >> 6)) * (1.0 / 9007199254740991.0);
        }
    }
}
