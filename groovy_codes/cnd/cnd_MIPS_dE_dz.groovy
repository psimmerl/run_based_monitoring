import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..3).collect{
  def gr = new GraphErrors('layer'+it+' Mean')
  gr.setTitle("MIPS dE/dz")
  gr.setTitleY("MIPS dE/dz (GeV/cm)")
  gr.setTitleX("run number")
  return gr
}

def grtl2 = (1..3).collect{
  def gr2 = new GraphErrors('layer'+it+' Sigma')
  gr2.setTitle("MIPS dE/dz")
  gr2.setTitleY("MIPS dE/dz (GeV/cm)")
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

  (0..<3).each{
      def h1 = dir.getObject(String.format("/cnd/CND_alignE_L%d_S%d_C%d",it+1,0+1,0+1))
      for(int sector=0;sector<24;sector++){
        for(int comp=0;comp<2;comp++){
               if(sector!=0||comp!=0){
                 def h2 = dir.getObject(String.format("/cnd/CND_alignE_L%d_S%d_C%d",it+1,sector+1,comp+1))
                 h1.add(h2)
               }
            }
        }
    h1.setName("layer"+(it+1));
    h1.setTitle("dE/dz (GeV/cm)")
    double maxE = h1.getBinContent(h1.getMaximumBin());
    // def f1 = ROOTFitter.fit(h1)
    f1=new F1D("fit:"+h1.getName(),"[amp]*gaus(x,[mean],[sigma])+[cst]", 0.0, 5.0);
    f1.setLineColor(33);
    f1.setLineWidth(10);
    // f1.setRange(1.5,5);
    f1.setParameter(1,2.0);
    f1.setParameter(0,maxE);
    f1.setParLimits(0,maxE*0.9,maxE*1.1);
    f1.setParameter(2,1.0);
    f1.setParameter(3,0.0);
    DataFitter.fit(f1,h1,"LQ")
    recursive_Gaussian_fitting(f1,h1)
    // def f1 = ROOTFitter.fit(h1)

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
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
grtl.each{ out.addDataSet(it) }
out.writeFile('cnd_dEdz_mean.hipo')
out2.mkdir('/timelines')
out2.cd('/timelines')
grtl2.each{ out2.addDataSet(it) }
out2.writeFile('cnd_dEdz_sigma.hipo')

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
        f1.setParameter(3,0.0);
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
