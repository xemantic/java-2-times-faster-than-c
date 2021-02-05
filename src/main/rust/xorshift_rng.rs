const ITERATION_COUNT: usize = 1000000000;

mod rng;
use rng::Xorshift64sRng;

fn main() {
    let mut rng = Xorshift64sRng::new(42);

    let checksum = std::iter::repeat_with(|| rng.get_rand())
        .take(ITERATION_COUNT)
        .sum::<f64>();

    println!("checksum: {}", checksum);
}

