import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  def gr = new GraphErrors('sec'+it)
  gr.setTitle("FTOF Vertex Time - RF Time, p1a pion, mean")
  gr.setTitleY("FTOF Vertex Time - RF Time, p1a pion, mean (ns)")
  gr.setTitleX("run number")
  return gr
}

def grtl2 = (1..6).collect{
  def gr = new GraphErrors('sec'+it)
  gr.setTitle("FTOF Vertex Time - RF Time, p1a pion, sigma")
  gr.setTitleY("FTOF Vertex Time - RF Time, p1a pion, sigma (ns)")
  gr.setTitleX("run number")
  return gr
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

  (0..<6).each{
    def h2 = dir.getObject('/FTOF/p1a_pad_vt_pion_S'+(it+1))
    def h1 = h2.projectionX()
    h1.setName("sec"+(it+1))
    h1.setTitle(h2.getTitle())
    h1.setTitleX(h2.getTitleX())

    // def f1 = ROOTFitter.fit(h1)
    def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])+[const]", -1.0, 1.0);
    f1.setLineWidth(2);
    f1.setOptStat("1111");
    initTimeGaussFitPar(f1,h1);
    DataFitter.fit(f1,h1,"LQ");
    recursive_Gaussian_fitting(f1,h1)
    grtl[it].addPoint(run, f1.getParameter(1), 0, 0)
    grtl2[it].addPoint(run, f1.getParameter(2), 0, 0)
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
out.writeFile('ftof_time_p1a_pion_mean.hipo')
out2.writeFile('ftof_time_p1a_pion_sigma.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = h1.getRMS(); //ns
        // double rangeMin = (hMean - (3*hRMS));
        // double rangeMax = (hMean + (3*hRMS));
        // double pm = hRMS;
        // f1.setRange(rangeMin, rangeMax);
        f1.setParameter(0, hAmp);
        f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        // f1.setParLimits(1, hMean-pm, hMean+(pm));
        f1.setParameter(2, hRMS);
        // f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
        f1.setParameter(3,0);
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
        if (f1.getChiSquare()>1000){
          initTimeGaussFitPar(f1,h1);
          DataFitter.fit(f1,h1,"LQ");
        }
}
