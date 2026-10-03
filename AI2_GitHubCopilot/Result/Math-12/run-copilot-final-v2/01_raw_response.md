TARGETS: BitsStreamGenerator.nextGaussian (cached Gaussian), nextDouble, abstract next(int),
hasNextGaussian/nextNextGaussian state
ORACLES: cloned generator must produce exactly the same sequence of random values (nextDouble,
nextGaussian, nextInt…) as the original after the clone point
CASES: clone after an odd number of nextGaussian calls (cache filled); clone after even count (cache
empty); clone following mixed nextInt/nextDouble calls; boundary: cached Gaussian = 0.0
RISKS: test should compare original vs clone subsequent outputs, not fixed literal values; risk that
underlying subclass state copy is not BitsStreamGenerator’s responsibility but still needed to pass