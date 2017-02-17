package nl.cha.batchjob;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import nl.cha.domein.IdeaRetourOntvangst;

public class IdearetourOntvangstItemProcessor implements ItemProcessor<IdeaRetourOntvangst, IdeaRetourOntvangst> {

	    private static final Logger log = LoggerFactory.getLogger(IdearetourOntvangstItemProcessor.class);

	    @Override
	    public IdeaRetourOntvangst process(final IdeaRetourOntvangst item) throws Exception {
	      
	        final IdeaRetourOntvangst transformedItem = new IdeaRetourOntvangst();

	        log.info("Zoek naar mailadres obv Factuurnummer....");

	        return transformedItem;
	    }

}
