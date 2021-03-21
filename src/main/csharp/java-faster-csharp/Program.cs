using System;
using System.Linq;
using XorShift;

namespace csharp
{
    static class Program
    {
        private const int  MaxPayloadSize   = 50;
        private const int  InitialNodeCount = 10000;
        private const long MutationCount    = 1000000L;
        private const int  MaxMutationSize  = 200;

        class Node
        {
            public Node Previous { get; private set; }
            public Node Next { get; private set; }
            public long Id { get; }
            public byte[] Payload { get; }

            public Node(long id, XorShift64SRng rng)
            {
                Id = id;
                int size = (int) (rng.getRand() * (double) MaxPayloadSize);
                byte[] data = new byte[size];
                for (int i = 0; i < size; i++)
                {
                    data[i] = (byte) i;
                }
                Payload = data;
            }

            public void Join(Node node)
            {
                Previous = node;
                Next = node;
                node.Previous = this;
                node.Next = this;
            }

            public void Delete()
            {
                Next.Previous = Previous;
                Previous.Next = Next;
            }

            public void Insert(Node node)
            {
                node.Next = Next;
                node.Previous = this;
                Next.Previous = node;
                Next = node;
            }

            // this needs to be here because without it memory leak :(
            ~Node()
            {
            }
        }

        static void Main(string[] args)
        {
            long nodeId = 0;
            XorShift64SRng rng = new XorShift64SRng(42);
            var head = new Node(nodeId++, rng);
            head.Join(new Node(nodeId++, rng));
            for (var i = 2; i < InitialNodeCount; i++)
            {
                head.Insert(new Node(nodeId++, rng));
            }

            long nodeCount = InitialNodeCount;
            for (long i = 0; i < MutationCount; i++)
            {
                var deleteCount = (int) (rng.getRand() * (double) MaxMutationSize);
                if (deleteCount > (nodeCount - 2))
                {
                    deleteCount = (int) nodeCount - 2;
                }

                for (int j = 0; j < deleteCount; j++)
                {
                    var toDelete = head;
                    head = head.Previous;
                    toDelete.Delete();
                }

                nodeCount -= deleteCount;
                var insertCount = (int) (rng.getRand() * (double) MaxMutationSize);
                for (int j = 0; j < insertCount; j++)
                {
                    head.Insert(new Node(nodeId++, rng));
                    head = head.Next;
                }

                nodeCount += insertCount;
            }

            long checksum = 0;
            var traveler = head;
            do
            {
                checksum += traveler.Id + traveler.Payload.Length;
                if (traveler.Payload.Length > 0)
                {
                    checksum += (SByte) traveler.Payload[0];        // byte in c# is unsigned, need to use Signed byte
                    checksum += (SByte) traveler.Payload[traveler.Payload.Length - 1];
                }
            } while (
                (traveler = traveler.Next) != head
            );

            Console.WriteLine("node count: " + nodeCount);
            Console.WriteLine("checksum: " + checksum);
        }
    }
}
