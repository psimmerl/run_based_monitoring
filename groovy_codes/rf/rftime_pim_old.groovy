import org.jlab.groot.data.TDirectory
import org.jlab.groot.data.GraphErrors
import org.jlab.groot.data.H1F
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.graphics.EmbeddedCanvas;

def grtl = (1..6).collect{
  def gr = new GraphErrors('sec'+it)
  gr.setTitle("Avearge #pi^- rftime1 per sector")
  gr.setTitleY("Average #pi^- rftime1 per sector (ns)")
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
    def h1 = dir.getObject('/RF/H_pim_RFtime1_S'+(it+1))
    grtl[it].addPoint(run, h1.getMean(), 0, 0)

    out.mkdir('/'+run)
    out.cd('/'+run)
    out.addDataSet(h1)
  }
}


out.mkdir('/timelines')
out.cd('/timelines')
grtl.each{ out.addDataSet(it) }
out.writeFile('rftime_pim.hipo')
