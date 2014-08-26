package eu.scape_project.planning.efficiency;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

public class LUndef extends CellProcessorAdaptor implements StringCellProcessor {
    
    public LUndef() {
        super();
    }

    public LUndef(final LongCellProcessor next) {
        super(next);
    }
    
    public static boolean isDefined(final long value) {
        return (value != Long.MAX_VALUE) && (value != Long.MIN_VALUE);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             if value is null or can't be parsed as a Long
     * @throws SuperCsvConstraintViolationException
     *             if value, or doesn't lie between min and max (inclusive)
     */
    public Object execute(final Object value, final CsvContext context) {
            validateInputNotNull(value, context);
            
            final Object result;
            if( value instanceof Long ) {
                Long longValue = (Long) value;
                if (isDefined(longValue.longValue())){
                    result = "" + value;                    
                } else {
                    result = "NaN";
                }
                    
            } else {
                    try {
                            result = Long.parseLong(value.toString());
                    }
                    catch(final NumberFormatException e) {
                            throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Long", value),
                                    context, this, e);
                    }
            }
            
            return next.execute(result, context);
    }

}
