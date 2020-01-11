import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  def gr = new GraphErrors('sec'+it)
  gr.setTitle("Mean FTOF edep per sector, p2")
  gr.setTitleY("Mean FTOF edep per sector, p2 (MeV)")
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
    def h1 = dir.getObject('/FTOF/p2_pad_edep_S'+(it+1))
    def f1 = new F1D("fit:"+h1.getName(),"[amp]*landau(x,[mean],[sigma])+[p0]*exp(-[p1]*x)", 0, 50.0);
    f1.setParameter(0,0.0);
    f1.setParameter(1,0.0);
    f1.setParameter(2,1.0);
    f1.setParameter(3,0.0);
    f1.setParameter(4,0.0);
    f1.setOptStat(1111111);
    f1.setLineWidth(2);

    initLandauFitPar(h1, f1);
    DataFitter.fit(f1,h1,"LRQ");

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[it].addPoint(run, f1.getParameter(1), 0, f1.getParameter(2))
    // grtl[it].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(f1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('ftof_edep_p2.hipo')

private void initLandauFitPar(H1F hcharge, F1D fcharge) {
        double hAmp  = hcharge.getBinContent(hcharge.getMaximumBin());
        double hMean = hcharge.getAxis().getBinCenter(hcharge.getMaximumBin());
        double hRMS  = hcharge.getRMS(); //ns
        fcharge.setRange(fcharge.getRange().getMin(), hMean*2.0);
        fcharge.setParameter(0, hAmp);
        fcharge.setParLimits(0, 0.5*hAmp, 1.5*hAmp);
        fcharge.setParameter(1, hMean);
        fcharge.setParLimits(1, 0.8*hMean, 1.2*hMean);//Changed from 5-30
        fcharge.setParameter(2, 0.3);//Changed from 2
        fcharge.setParLimits(2, 0.1, 1);//Changed from 0.5-10
        fcharge.setParLimits(3,0, hAmp);
        fcharge.setParLimits(4,0,100);
}
