import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

  def grtl = new GraphErrors('Mean')
  grtl.setTitle("FTC time - start time, neutral (peak value)")
  grtl.setTitleY("FTC time - start time, neutral (peak value) (ns)")
  grtl.setTitleX("run number")

  def grtl2 = new GraphErrors('Sigma')
  grtl2.setTitle("FTC time - start time, neutral (sigma)")
  grtl2.setTitleY("FTC time - start time, neutral (sigma) (ns)")
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

  def h1 = dir.getObject('/ft/hi_cal_time_cut_neu')
  // def f1 = ROOTFitter.fit(h1)
  def ftime_neu = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])", -1.0, 1.0);
  ftime_neu.setParameter(0, 0.0);
  ftime_neu.setParameter(1, 0.0);
  ftime_neu.setParameter(2, 2.0);
  ftime_neu.setLineWidth(2);
  ftime_neu.setOptStat("1111");
  initTimeGaussFitPar(ftime_neu,h1);
  DataFitter.fit(ftime_neu,h1,"LQ");

  //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
  grtl.addPoint(run, ftime_neu.getParameter(1), 0, 0)
  grtl2.addPoint(run, ftime_neu.getParameter(2), 0, 0)
  // grtl.addPoint(run, h1.getMean(), 0, 0)
  // grtl2.addPoint(run, h1.getRMS(), 0, 0)
  out.addDataSet(h1)
  out.addDataSet(ftime_neu)
  out2.addDataSet(h1)
  out2.addDataSet(ftime_neu)

}


out.mkdir('/timelines')
out.cd('/timelines')
out2.mkdir('/timelines')
out2.cd('/timelines')
grtl.each{ out.addDataSet(it) }
grtl2.each{ out2.addDataSet(it) }
out.writeFile('ftc_time_neu_mean.hipo')
out2.writeFile('ftc_time_neu_sigma.hipo')


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
