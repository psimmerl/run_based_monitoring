import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = new GraphErrors('Mean')
grtl.setTitle("#pi^- time - start time")
grtl.setTitleY("#pi^- time - start time (ns)")
grtl.setTitleX("run number")

def grtl2 = new GraphErrors('Sigma')
grtl2.setTitle("#pi^- time - start time")
grtl2.setTitleY("#pi^- time - start time (ns)")
grtl2.setTitleX("run number")

TDirectory out = new TDirectory()
TDirectory out2 = new TDirectory()

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  def h1 = dir.getObject('/tof/H_pim_vtd')
  def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", -1.0, 1.0);
  f1.setLineWidth(2);
  f1.setOptStat("1111");
  initTimeGaussFitPar(f1,h1);
  DataFitter.fit(f1,h1,"LQ");
  recursive_Gaussian_fitting(f1,h1)
  grtl.addPoint(run, f1.getParameter(1), 0, 0)
  grtl2.addPoint(run, f1.getParameter(2), 0, 0)
  // grtl.addPoint(run, h1.getMean(), 0, 0)
  // grtl2.addPoint(run, h1.getRMS(), 0, 0)

  out.mkdir('/'+run)
  out.cd('/'+run)
  out.addDataSet(h1)
  out.addDataSet(f1)
  out2.mkdir('/'+run)
  out2.cd('/'+run)
  out2.addDataSet(h1)
  out2.addDataSet(f1)
}

out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('ec_pim_time_mean.hipo')
out2.mkdir('/timelines')
out2.cd('/timelines')
grtl2.each{ out2.addDataSet(it) }
out2.writeFile('ec_pim_time_sigma.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = h1.getRMS(); //ns
        // double rangeMin = (hMean - (3*hRMS));
        // double rangeMax = (hMean + (3*hRMS));
        // double pm = hRMS;
        // f1.setRange(rangeMin, rangeMax);
        f1.setParameter(0, hAmp);
        // f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        // f1.setParLimits(1, hMean-pm, hMean+(pm));
        f1.setParameter(2, hRMS);
        // f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
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
        System.out.println("chi2 too large")
        if (f1.getChiSquare()>500){
          initTimeGaussFitPar(f1,h1);
          DataFitter.fit(f1,h1,"LQ");
        }
}
