import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def data = []

for(arg in args) {
  TDirectory dir = new TDirectory()
  dir.readFile(arg)

  def name = arg.split('/')[-1]
  def m = name =~ /\d\d\d\d/
  def run = m[0].toInteger()

  def rr = [run:run, mean:[], sigma:[], h1:[], f1:[]]
  (0..<6).each{
    def h1 = dir.getObject('/RF/H_e_RFtime1_S'+(it+1))
    def f1 = RFFitter.fit(h1)
    rr.h1.add(h1)
    rr.f1.add(f1)
    rr.mean.add(f1.getParameter(1))
    rr.sigma.add(f1.getParameter(2).abs())
  }
  data.add(rr)
}


['mean', 'sigma'].each{name ->
  TDirectory out = new TDirectory()

  def grtl = (1..6).collect{
    def gr = new GraphErrors('sec'+it)
    gr.setTitle("Average electron rftime1 per sector, FD, "+name)
    gr.setTitleY("Average electron rftime1 per sector, FD (ns)")
    gr.setTitleX("run number")
    return gr
  }

  data.each{rr->
    out.mkdir('/'+rr.run)
    out.cd('/'+rr.run)

    rr.h1.each{ out.addDataSet(it) }
    rr.f1.each{ out.addDataSet(it) }
    6.times{
      grtl[it].addPoint(rr.run, rr[name][it], 0, 0)
    }
  }

  out.mkdir('/timelines')
  out.cd('/timelines')
  grtl.each{ out.addDataSet(it) }
  out.writeFile('rftime_electron_FD_'+name+'.hipo')
}

