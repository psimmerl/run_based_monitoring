import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl1 = (1..6).collect{
  sec_num = it
  def gr1 = new GraphErrors('sec'+sec_num+' sl'+1)
  gr1.setTitle("DC residuals (peak value) per sector per superlayer")
  gr1.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr1.setTitleX("run number")
  return gr1
}
def grtl2 = (1..6).collect{
  sec_num = it
  def gr2 = new GraphErrors('sec'+sec_num+' sl'+2)
  gr2.setTitle("DC residuals (peak value) per sector per superlayer")
  gr2.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr2.setTitleX("run number")
  return gr2
}
def grtl3 = (1..6).collect{
  sec_num = it
  def gr3 = new GraphErrors('sec'+sec_num+' sl'+3)
  gr3.setTitle("DC residuals (peak value) per sector per superlayer")
  gr3.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr3.setTitleX("run number")
  return gr3
}
def grtl4 = (1..6).collect{
  sec_num = it
  def gr4 = new GraphErrors('sec'+sec_num+' sl'+4)
  gr4.setTitle("DC residuals (peak value) per sector per superlayer")
  gr4.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr4.setTitleX("run number")
  return gr4
}
def grtl5 = (1..6).collect{
  sec_num = it
  def gr5 = new GraphErrors('sec'+sec_num+' sl'+5)
  gr5.setTitle("DC residuals (peak value) per sector per superlayer")
  gr5.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr5.setTitleX("run number")
  return gr5
}
def grtl6 = (1..6).collect{
  sec_num = it
  def gr6 = new GraphErrors('sec'+sec_num+' sl'+6)
  gr6.setTitle("DC residuals (peak value) per sector per superlayer")
  gr6.setTitleY("DC residuals (peak value) per sector per superlayer (cm)")
  gr6.setTitleX("run number")
  return gr6
}

def grtl7 = (1..6).collect{
  sec_num = it
  def gr1 = new GraphErrors('sec'+sec_num+' sl'+1)
  gr1.setTitle("DC residuals (sigma) per sector per superlayer")
  gr1.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr1.setTitleX("run number")
  return gr1
}
def grtl8 = (1..6).collect{
  sec_num = it
  def gr2 = new GraphErrors('sec'+sec_num+' sl'+2)
  gr2.setTitle("DC residuals (sigma) per sector per superlayer")
  gr2.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr2.setTitleX("run number")
  return gr2
}
def grtl9 = (1..6).collect{
  sec_num = it
  def gr3 = new GraphErrors('sec'+sec_num+' sl'+3)
  gr3.setTitle("DC residuals (sigma) per sector per superlayer")
  gr3.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr3.setTitleX("run number")
  return gr3
}
def grtl10 = (1..6).collect{
  sec_num = it
  def gr4 = new GraphErrors('sec'+sec_num+' sl'+4)
  gr4.setTitle("DC residuals (sigma) per sector per superlayer")
  gr4.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr4.setTitleX("run number")
  return gr4
}
def grtl11 = (1..6).collect{
  sec_num = it
  def gr5 = new GraphErrors('sec'+sec_num+' sl'+5)
  gr5.setTitle("DC residuals (sigma) per sector per superlayer")
  gr5.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr5.setTitleX("run number")
  return gr5
}
def grtl12 = (1..6).collect{
  sec_num = it
  def gr6 = new GraphErrors('sec'+sec_num+' sl'+6)
  gr6.setTitle("DC residuals (sigma) per sector per superlayer")
  gr6.setTitleY("DC residuals (sigma) per sector per superlayer (cm)")
  gr6.setTitleX("run number")
  return gr6
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
    sec_num = (it+1)
    def h21 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_1',sec_num))
    def h11 = h21.projectionY()
    def h22 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_2',sec_num))
    def h12 = h22.projectionY()
    def h23 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_3',sec_num))
    def h13 = h23.projectionY()
    def h24 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_4',sec_num))
    def h14 = h24.projectionY()
    def h25 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_5',sec_num))
    def h15 = h25.projectionY()
    def h26 = dir.getObject(String.format('/dc/DC_residuals_trkDoca_%d_6',sec_num))
    def h16 = h26.projectionY()
    h11.setName("sec"+sec_num+"sl"+1)
    h11.setTitle("DC residuals per sector per superlayer")
    h11.setTitleX("DC residuals per sector per superlayer (cm)")
    h12.setName("sec"+sec_num+"sl"+2)
    h12.setTitle("DC residuals per sector per superlayer")
    h12.setTitleX("DC residuals per sector per superlayer (cm)")
    h13.setName("sec"+sec_num+"sl"+3)
    h13.setTitle("DC residuals per sector per superlayer")
    h13.setTitleX("DC residuals per sector per superlayer (cm)")
    h14.setName("sec"+sec_num+"sl"+4)
    h14.setTitle("DC residuals per sector per superlayer")
    h14.setTitleX("DC residuals per sector per superlayer (cm)")
    h15.setName("sec"+sec_num+"sl"+5)
    h15.setTitle("DC residuals per sector per superlayer")
    h15.setTitleX("DC residuals per sector per superlayer (cm)")
    h16.setName("sec"+sec_num+"sl"+6)
    h16.setTitle("DC residuals per sector per superlayer")
    h16.setTitleX("DC residuals per sector per superlayer (cm)")

    // def f1 = ROOTFitter.fit(h1)
    def f11 = new F1D("f11", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f11.setName("fit:"+h11.getName())
    f11.setLineWidth(2);
    f11.setOptStat("1111");
    initTimeGaussFitPar(f11,h11);
    DataFitter.fit(f11,h11,"LQ");
    recursive_Gaussian_fitting(f11,h11)

    def f12 = new F1D("f12", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f12.setName("fit:"+h12.getName())
    f12.setLineWidth(2);
    f12.setOptStat("1111");
    initTimeGaussFitPar(f12,h12);
    DataFitter.fit(f12,h12,"LQ");
    recursive_Gaussian_fitting(f12,h12)

    def f13 = new F1D("f13", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f13.setName("fit:"+h13.getName())
    f13.setLineWidth(3);
    f13.setOptStat("1111");
    initTimeGaussFitPar(f13,h13);
    DataFitter.fit(f13,h13,"LQ");
    recursive_Gaussian_fitting(f13,h13)

    def f14 = new F1D("f14", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f14.setName("fit:"+h14.getName())
    f14.setLineWidth(3);
    f14.setOptStat("1111");
    initTimeGaussFitPar(f14,h14);
    DataFitter.fit(f14,h14,"LQ");
    recursive_Gaussian_fitting(f14,h14)

    def f15 = new F1D("f15", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f15.setName("fit:"+h15.getName())
    f15.setLineWidth(3);
    f15.setOptStat("1111");
    initTimeGaussFitPar(f15,h15);
    DataFitter.fit(f15,h15,"LQ");
    recursive_Gaussian_fitting(f15,h15)

    def f16 = new F1D("f16", "[amp]*gaus(x,[mean],[sigma])+[const]",-0.5,0.5);
    f16.setName("fit:"+h16.getName())
    f16.setLineWidth(3);
    f16.setOptStat("1111");
    initTimeGaussFitPar(f16,h16);
    DataFitter.fit(f16,h16,"LQ");
    recursive_Gaussian_fitting(f16,h16)

    //grtl[it].addPoint(run, h1.getDataX(h1.getMaximumBin()), 0, 0)
    grtl1[it].addPoint(run, f11.getParameter(1), 0, 0)
    out.addDataSet(h11)
    out.addDataSet(f11)
    grtl2[it].addPoint(run, f12.getParameter(1), 0, 0)
    out.addDataSet(h12)
    out.addDataSet(f12)
    grtl3[it].addPoint(run, f13.getParameter(1), 0, 0)
    out.addDataSet(h13)
    out.addDataSet(f13)
    grtl4[it].addPoint(run, f14.getParameter(1), 0, 0)
    out.addDataSet(h14)
    out.addDataSet(f14)
    grtl5[it].addPoint(run, f15.getParameter(1), 0, 0)
    out.addDataSet(h15)
    out.addDataSet(f15)
    grtl6[it].addPoint(run, f16.getParameter(1), 0, 0)
    out.addDataSet(h16)
    out.addDataSet(f16)

    grtl7[it].addPoint(run, f11.getParameter(2), 0, 0)
    out2.addDataSet(h11)
    out2.addDataSet(f11)
    grtl8[it].addPoint(run, f12.getParameter(2), 0, 0)
    out2.addDataSet(h12)
    out2.addDataSet(f12)
    grtl9[it].addPoint(run, f13.getParameter(2), 0, 0)
    out2.addDataSet(h13)
    out2.addDataSet(f13)
    grtl10[it].addPoint(run, f14.getParameter(2), 0, 0)
    out2.addDataSet(h14)
    out2.addDataSet(f14)
    grtl11[it].addPoint(run, f15.getParameter(2), 0, 0)
    out2.addDataSet(h15)
    out2.addDataSet(f15)
    grtl12[it].addPoint(run, f16.getParameter(2), 0, 0)
    out2.addDataSet(h16)
    out2.addDataSet(f16)

  }
}


out.mkdir('/timelines')
out.cd('/timelines')

out2.mkdir('/timelines')
out2.cd('/timelines')

grtl1.each{ out.addDataSet(it) }
grtl2.each{ out.addDataSet(it) }
grtl3.each{ out.addDataSet(it) }
grtl4.each{ out.addDataSet(it) }
grtl5.each{ out.addDataSet(it) }
grtl6.each{ out.addDataSet(it) }

grtl7.each{ out2.addDataSet(it) }
grtl8.each{ out2.addDataSet(it) }
grtl9.each{ out2.addDataSet(it) }
grtl10.each{ out2.addDataSet(it) }
grtl11.each{ out2.addDataSet(it) }
grtl12.each{ out2.addDataSet(it) }

out.writeFile('dc_residuals_sec_sl_peak.hipo')
out2.writeFile('dc_residuals_sec_sl_sigma.hipo')

private void initTimeGaussFitPar(F1D f1, H1F h1) {
        double hAmp  = h1.getBinContent(h1.getMaximumBin());
        double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin());
        double hRMS  = h1.getRMS(); //ns
        // double rangeMin = (hMean - (3*hRMS));
        // double rangeMax = (hMean + (3*hRMS));
        // double pm = hRMS;
        f1.setRange(hMean-1, hMean+1);
        f1.setParameter(0, hAmp);
        //f1.setParLimits(0, hAmp*0.8, hAmp*1.2);
        f1.setParameter(1, hMean);
        f1.setParLimits(1, hMean-0.5, hMean+0.5);
        f1.setParameter(2, hRMS);
        //f1.setParLimits(2, 0.1*hRMS, 0.8*hRMS);
        f1.setParameter(3,0);
}

private void recursive_Gaussian_fitting(F1D f1, H1F h1){
        double rangeMin = f1.getParameter(1)-2*f1.getParameter(2)
        double rangeMax = f1.getParameter(1)+2*f1.getParameter(2)
        // limit fitting range as 2 sigma
        def f2 = new F1D("temp", "[amp]*gaus(x,[mean],[sigma])+[const]", -0.5, 0.5);
        f2=f1
        f2.setRange(rangeMin,rangeMax)
        DataFitter.fit(f1,h1,"LQ");
        if (f1.getChiSquare()>f2.getChiSquare()){
          System.out.println("Replacing fitting function")
          f1=f2
          f1.setName("fit:"+h1.getName())
        }
}
