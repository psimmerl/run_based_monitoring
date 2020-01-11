import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  def gr = new GraphErrors('sec'+it)
  gr.setTitle("VZ (peak value) per sector")
  gr.setTitleY("VZ (peak value) per sector")
  gr.setTitleX("run number")
  return gr
}

TDirectory out = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)

  (0..<6).each{
    // def h2 = dir.getObject('/elec/H_trig_vz_mom_S'+(it+1))
    // def h1 = h2.projectionY()
    def h1 = dir.getObject('/dc/H_dcm_vz_s'+(it+1))
    // h1.setName("sec"+(it+1))
    // h1.setTitle("VZ for negative")
    h1.setTitleX("VZ for negative (cm)")

    // def f1 = ROOTFitter.fit(h1)
    def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", -20.0, 10.0);
    f1.setLineWidth(2);
    f1.setOptStat("1111");
    initTimeGaussFitPar(f1,h1);
    DataFitter.fit(f1,h1,"LQ");

    // grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    // grtl[it].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('forward_negative_VZ.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = h1.getRMS(); //ns
        double rangeMin = (hMean - (3*hRMS));
        double rangeMax = (hMean + (3*hRMS));
        // double pm = hRMS;
        f1.setRange(rangeMin, rangeMax);
        f1.setParameter(0, hAmp);
        // f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        // f1.setParLimits(1, hMean-pm, hMean+(pm));
        f1.setParameter(2, hRMS);
        // f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
}
