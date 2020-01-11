import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.math.RandomFunc;

// import ROOTFitter
def grtl = (1..2).collect{
  def gr = new GraphErrors('layer'+it)
  gr.setTitle("FTH MIPS time per layer (peak value)")
  gr.setTitleY("FTH MIPS time per layer (peak value) (ns)")
  gr.setTitleX("run number")
  return gr
}

def grtl2 = (1..2).collect{
  def gr2 = new GraphErrors('layer'+it)
  gr2.setTitle("FTH MIPS time per layer (sigma)")
  gr2.setTitleY("FTH MIPS time per layer (sigma) (ns)")
  gr2.setTitleX("run number")
  return gr2
}

TDirectory out = new TDirectory()
TDirectory out2 = new TDirectory()
for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  out.mkdir('/'+run)
  out.cd('/'+run)
  out2.mkdir('/'+run)
  out2.cd('/'+run)
  (0..<2).each{
    def h1 = dir.getObject('/ft/hi_hodo_tmatch_l'+(it+1))
    def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])+[const]", -10.0, 10.0);
    f1.setParameter(0, 0.0);
    f1.setParameter(1, 0.0);
    f1.setParameter(2, 2.0);
    f1.setParameter(3, h1.getMin());
    f1.setLineWidth(2);
    f1.setOptStat("1111");
    initTimeGaussFitPar(f1,h1);
    DataFitter.fit(f1,h1,"LQ");
    recursive_Gaussian_fitting(f1,h1)
   // def h1 = h2.projectionY()
    // h1.setName("layer"+(it+1))
    // h1.setTitle("FTH_MIPS_energy")
    // h1.setTitleX("E (MeV)")

    // def f1 = ROOTFitter.fit(h1)

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    grtl2[it].addPoint(run, f1.getParameter(2), 0, 0)
    // grtl[it].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(f1)
    out2.addDataSet(h1)
    out2.addDataSet(f1)
  }
}
out.mkdir('/timelines')
out.cd('/timelines')
out2.mkdir('/timelines')
out2.cd('/timelines')
grtl.each{ out.addDataSet(it) }
grtl2.each{ out2.addDataSet(it) }
out.writeFile('fth_MIPS_time_mean.hipo')
out2.writeFile('fth_MIPS_time_sigma.hipo')

private void initTimeGaussFitPar(F1D ftime, H1F htime) {
        double hAmp  = htime.getBinContent(htime.getMaximumBin());
        double hMean = htime.getAxis().getBinCenter(htime.getMaximumBin());
        double hRMS  = htime.getRMS(); //ns
        double rangeMin = (hMean - (3*hRMS));
        double rangeMax = (hMean + (3*hRMS));
        double pm = hRMS*3;
        ftime.setRange(rangeMin, rangeMax);
        ftime.setParameter(0, hAmp);
        ftime.setParLimits(0, hAmp*0.9, hAmp*1.1);
        ftime.setParameter(1, hMean);
        ftime.setParLimits(1, hMean-pm, hMean+(pm));
        ftime.setParameter(2, 0.2);
        ftime.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
}

private void recursive_Gaussian_fitting(F1D f1, H1F h1){
        double rangeMin = f1.getParameter(1)-2*f1.getParameter(2)
        double rangeMax = f1.getParameter(1)+2*f1.getParameter(2)
        // limit fitting range as 2 sigma
        f1.setRange(rangeMin, rangeMax)
        // if with noise, don't fit such noise
        if(f1.getNPars()>3){
          (3..f1.getNPars()-1).each{
            f1.setParLimits(it,f1.getParameter(it)*0.8, f1.getParameter(it)*1.2)
          }
        }
        DataFitter.fit(f1,h1,"LQ");
        if (f1.getChiSquare()>500){
          System.out.println("chi2 too large")
          initTimeGaussFitPar(f1,h1);
          DataFitter.fit(f1,h1,"LQ");
        }
}
