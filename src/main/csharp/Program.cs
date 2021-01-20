using System;

namespace csharp
{
    static class Program
    {
        private const int NodeCount = 1000;
        private const long TraversalCount = 5000000000L;

        class Node : IDisposable {
            public Node Previous { get; private set; }
            public Node Next { get; private set; }
            public long Id { get; }

            public Node(long id) {
                this.Id = id;
            }

            public void Join(Node node) {
                Previous = node;
                Next = node;
                node.Previous = this;
                node.Next = this;
            }

            public void Delete() {
                Next.Previous = Previous;
                Previous.Next = Next;
            }

            public void Insert(Node node) {
                node.Next = Next;
                node.Previous = this;
                Next.Previous = node;
                Next = node;
            }

            public void Dispose()
            {
            }
        }

        static void Main(string[] args)
        {
            long nodeId = 0;
            var head = new Node(nodeId++);
            head.Join(new Node(nodeId++));
            for (int i = 2; i < NodeCount; i++) {
                head.Insert(new Node(nodeId++));
            }
            var toDelete = head;
            var toInsert = head;
            for (long i = 0; i < TraversalCount; i++) {
                toInsert = toInsert.Next;
                Node prevToDelete = toDelete.Previous;
                if (toInsert == toDelete) {
                    toInsert = toInsert.Next;
                }
                toDelete.Delete();
                toDelete.Dispose();
                toDelete = prevToDelete;
                toInsert.Insert(new Node(nodeId++));
            }
            long checksum = 0;
            head = toInsert;
            var traveler = head;
            do {
                checksum += traveler.Id;
            } while (
                (traveler = traveler.Next) != head
            );
            System.Console.WriteLine("checksum: " + checksum);
        }
    }
}
