import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..30).collect{
  board=(it-1)%15+1
  layer=(it-1).intdiv(15)+1
  def gr = new GraphErrors('layer'+layer+'board'+board)
  gr.setTitle("FTH MIPS energy per layer per board (peak value)")
  gr.setTitleY("FTH MIPS energy per layer per board (peak value) (MeV)")
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

for (l = 0; l <2; l++) {
  for (b = 0; b <15; b++) {
    counter=l*15+b
    layer = l+1
    board = b+1
    def h1 = dir.getObject('/ft/hi_hodo_ematch_l'+(layer)+'_b'+(board))
    def f_charge_landau = new F1D("fit:"+h1.getName(),"[amp]*landau(x,[mean],[sigma])+[p0]+[p1]*x", 0.5*(l+1), 10.0);
    f_charge_landau.setParameter(0,0.0);
    f_charge_landau.setParameter(1,0.0);
    f_charge_landau.setParameter(2,1.0);
    f_charge_landau.setParameter(3,0.0);
    f_charge_landau.setParameter(4,0.0);
    f_charge_landau.setOptStat(1111111);
    f_charge_landau.setLineWidth(2);

    initLandauFitPar(h1, f_charge_landau);
    DataFitter.fit(f_charge_landau,h1,"LRQ");
   // def h1 = h2.projectionY()
    // h1.setName("layer"+(it+1))
    // h1.setTitle("FTH_MIPS_energy")
    // h1.setTitleX("E (MeV)")

    // def f1 = ROOTFitter.fit(h1)

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl[counter].addPoint(run, f_charge_landau.getParameter(1), 0, 0)
    // grtl[it].addPoint(run, h1.getMean(), 0, 0)
    out.addDataSet(h1)
    out.addDataSet(f_charge_landau)
  }
}
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('fth_MIPS_energy_board.hipo')

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
        fcharge.setParameter(3, 0.2*hAmp);
        fcharge.setParameter(4, -0.3);//Changed from -0.2
}
