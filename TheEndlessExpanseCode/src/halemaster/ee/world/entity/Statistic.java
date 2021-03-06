package halemaster.ee.world.entity;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @name Statistic
 * 
 * @version 0.0.0
 * 
 * @date Mar 12, 2014
 */
public enum Statistic 
{
  LEVEL (new Statistic[]{}, "", -1),
  XP (new Statistic[]{}, "", 100),
  POWER (new Statistic[]{}, "", -1),
  AGILITY (new Statistic[]{}, "", -1),
  INTELLECT (new Statistic[]{}, "", -1),
  HEALTH (new Statistic[]{LEVEL}, "12+(LEVEL^1.75+log(LEVEL))", -1),
  STRIKE (new Statistic[]{POWER, LEVEL}, "POWER*1.10+LEVEL/20", -1),
  AIM (new Statistic[]{AGILITY, LEVEL}, "AGILITY*1.10+LEVEL/20", -1),
  MAGIK (new Statistic[]{INTELLECT, LEVEL}, "INTELLECT*1.10+LEVEL/20", -1),
  ARMOR (new Statistic[]{POWER, LEVEL}, "POWER/2*(1+LEVEL/20)", -1),
  ACCURACY (new Statistic[]{AGILITY, LEVEL}, "AGILITY/2*(1+LEVEL/20)+LEVEL", -1),
  CRAFT (new Statistic[]{INTELLECT, LEVEL}, "INTELLECT/2*(1+LEVEL/20)", -1),
  CRITICAL (new Statistic[]{}, "3", 99),
  HASTE (new Statistic[]{}, "0", 90),
  RANGE (new Statistic[]{}, String.valueOf (Entity.MELEE_RANGE), 24);
  
  public static final Logger LOGGER = 
          Logger.getLogger (Statistic.class.getName ());
  public static final int NONE = -1;
  
  private Statistic[] derived;
  private ExpressionBuilder formula;
  private int maximum;
  
  private Statistic (Statistic[] derived, String formula, int maximum)
  {
    this.derived = derived;
    this.maximum = maximum;
    if (!"".equals (formula))
    {
      this.formula = new ExpressionBuilder(formula);
      for (Statistic derive : derived)
      {
        this.formula = this.formula.withVariableNames (derive.name ());
      }
    }
    else
    {
      this.formula = null;
    }
  }
  
  /**
   * Derive the Statistic with the given base statistic values
   * @param derivers base statistic values
   * @return the value of this derived value
   */
  public int derive (int[] derivers)
  {
    int value = -1;
    
    if (null != this.formula)
    {
      try
      {
        Calculable calc = this.formula.build ();
        for (int i = 0; i < this.derived.length; i++)
        {
          calc.setVariable (this.derived[i].name (), derivers[i]);
        }
        value = (int) calc.calculate ();
      }
      catch (UnknownFunctionException e)
      {
        LOGGER.log (Level.SEVERE, "Can't derive Statistic!", e);
      }
      catch (UnparsableExpressionException e)
      {
        LOGGER.log (Level.SEVERE, "Can't derive Statistic!", e);
      }
    }
    else
    {
      value = derivers[0];
    }
    
    return value;
  }
  
  public final Statistic[] getDerived ()
  {
    return this.derived;
  }
  
  public final int getMaximum ()
  {
    return this.maximum;
  }
}
