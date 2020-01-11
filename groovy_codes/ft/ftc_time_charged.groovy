import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

  def grtl = new GraphErrors('Mean')
  grtl.setTitle("FTC time - start time, charged (peak value)")
  grtl.setTitleY("FTC time - start time, charged (peak value) (ns)")
  grtl.setTitleX("run number")

  def grtl2 = new GraphErrors('Sigma')
  grtl2.setTitle("FTC time - start time, charged (sigma)")
  grtl2.setTitleY("FTC time - start time, charged (sigma) (ns)")
  grtl2.setTitleX("run number")


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

  def h1 = dir.getObject('/ft/hi_cal_time_cut_ch')
  // def f1 = ROOTFitter.fit(h1)
  def ftime_ch = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", -1.0, 1.0);
  ftime_ch.setParameter(0, 0.0);
  ftime_ch.setParameter(1, 0.0);
  ftime_ch.setParameter(2, 2.0);
  ftime_ch.setLineWidth(2);
  ftime_ch.setOptStat("1111");
  initTimeGaussFitPar(ftime_ch,h1);
  DataFitter.fit(ftime_ch,h1,"LQ");
  recursive_Gaussian_fitting(ftime_ch,h1)

  //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
  grtl.addPoint(run, ftime_ch.getParameter(1), 0, 0)
  grtl2.addPoint(run, ftime_ch.getParameter(2), 0, 0)
  // grtl.addPoint(run, h1.getMean(), 0, 0)
  // grtl2.addPoint(run, h1.getRMS(), 0, 0)
  out.addDataSet(h1)
  out.addDataSet(ftime_ch)
  out2.addDataSet(h1)
  out2.addDataSet(ftime_ch)

}

out.mkdir('/timelines')
out.cd('/timelines')
out2.mkdir('/timelines')
out2.cd('/timelines')
grtl.each{ out.addDataSet(it) }
grtl2.each{ out2.addDataSet(it) }
out.writeFile('ftc_time_ch_mean.hipo')
out2.writeFile('ftc_time_ch_sigma.hipo')


private void initTimeGaussFitPar(F1D ftime, H1F htime) {
        double hAmp  = htime.getBinContent(htime.getMaximumBin());
        double hMean = htime.getAxis().getBinCenter(htime.getMaximumBin());
        double hRMS  = htime.getRMS(); //ns
        double rangeMin = (hMean - (3*hRMS));
        double rangeMax = (hMean + (3*hRMS));
        double pm = hRMS*3;
        ftime.setRange(rangeMin, rangeMax);
        ftime.setParameter(0, hAmp);
        ftime.setParLimits(0, hAmp*0.8, hAmp*1.2);
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
