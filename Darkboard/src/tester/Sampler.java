package tester;

public class Sampler {
   Sampler.Sample[] samples;

   public Sampler(int sampleSet) {
      this.samples = new Sampler.Sample[sampleSet];

      for(int k = 0; k < sampleSet; ++k) {
         this.samples[k] = new Sampler.Sample();
      }

   }

   public void addSample(int k, double val) {
      if (k >= 0 && k < this.samples.length) {
         this.samples[k].addSample(val);
      }

   }

   public String toString() {
      String s = "Samples: " + this.samples[0].samples + "\n";

      for(int k = 0; k < this.samples.length; ++k) {
         s = s + this.samples[k].toString() + "\n";
      }

      return s;
   }

   public class Sample {
      double accumulatedValue = 0.0D;
      int samples = 0;

      double getValue() {
         return this.accumulatedValue / (double)this.samples;
      }

      void addSample(double val) {
         this.accumulatedValue += val;
         ++this.samples;
      }

      public String toString() {
         return "" + this.getValue();
      }
   }
}
