import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.data.H1F
import org.jlab.groot.math.F1D


class CTOFFitter {
  static F1D fit(H1F h1) {
    double hMean = h1.getAxis().getBinCenter(h1.getMaximumBin())
    double xmin = hMean-0.2
    double xmax = hMean+0.2
    double hRMS  = 0.08
    def f1 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])+[const]",xmin,xmax);

    f1.setParameter(1, hMean)
    f1.setParameter(2, hRMS)
    f1.setParLimits(2, 0, 0.4)
    f1.setParameter(3, 0)
    f1.setParLimits(3, 0, h1.getMax()*0.2)

    def makefits = {func->
      hRMS = func.getParameter(2).abs()
      func.setRange(hMean-2.5*hRMS, hMean+2.5*hRMS)
      DataFitter.fit(func,h1,"Q")
      return [func.getChiSquare(), (0..<func.getNPars()).collect{func.getParameter(it)}]
    }
    def fits1 = (0..10).collect{makefits(f1)}

    def f2 = new F1D("fit:"+h1.getName(), "[amp]*gaus(x,[mean],[sigma])+[const]+[slope]*x",-0.2,0.2);

    fits1.sort()[0][1].eachWithIndex{par,ipar->
      f2.setParameter(ipar, par)
    }

    def fits2 = (0..10).collect{makefits(f2)}

    def bestfit = fits2.sort()[0]
    f2.setParameters(*bestfit[1])

    return f2
  }

}
