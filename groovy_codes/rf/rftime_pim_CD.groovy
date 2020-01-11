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

  def h1 = dir.getObject('/RF/H_pim_RFtime1')
  def f1 = RFFitter.fit(h1)

  data.add([run:run, mean:f1.getParameter(1), sigma:f1.getParameter(2).abs(), h1:h1, f1:f1])
}


['mean', 'sigma'].each{name ->
  TDirectory out = new TDirectory()

  def grtl = new GraphErrors('RFtime_pim_CD'+name)
  grtl.setTitle("Average #pi<sup>-</sup> rftime1, CD ("+name+")")
  grtl.setTitleY("Average #pi<sup>-</sup> rftime1, CD (ns)")
  grtl.setTitleX("run number")

  data.each{
    grtl.addPoint(it.run, it[name], 0, 0)
    out.mkdir('/'+it.run)
    out.cd('/'+it.run)
    out.addDataSet(it.h1)
    out.addDataSet(it.f1)
  }

  out.mkdir('/timelines')
  out.cd('/timelines')
  out.addDataSet(grtl)
  out.writeFile('rftime_pim_CD_'+name+'.hipo')
}

