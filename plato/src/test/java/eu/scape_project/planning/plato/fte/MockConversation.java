package eu.scape_project.planning.plato.fte;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.context.ManagedConversation;

@Alternative
@ConversationScoped
public class MockConversation implements ManagedConversation, Serializable {

	 private static Set<String> ACTIVE_CONVERSATIONS = new HashSet<String>();
	 
	    private static long CONVERSATION_ID_COUNTER = 1;
	 
	    private static final Logger log = Logger.getLogger(MockConversation.class.getName());
	 
	    private boolean _transient;
	 
	    private BeanManager beanManager;
	 
	    private String id;
	 
	    private long timeout;
	 
	// --------------------------- CONSTRUCTORS ---------------------------
	 
	    public MockConversation()
	    {
	    }
	 
	    @Inject
	    public MockConversation(BeanManager beanManager)
	    {
	        this.beanManager = beanManager;
	        this._transient = true;
	        this.timeout = 0;
	    }
	 
	// --------------------- GETTER / SETTER METHODS ---------------------
	 
	    public long getTimeout()
	    {
	        verifyConversationContextActive();
	        return timeout;
	    }
	 
	    public void setTimeout(long timeout)
	    {
	        verifyConversationContextActive();
	        this.timeout = timeout;
	    }
	 
	// ------------------------ CANONICAL METHODS ------------------------
	 
	    @Override
	    public String toString()
	    {
	        if (_transient) {
	            return "Transient conversation";
	        } else {
	            return "Conversation with id: " + id;
	        }
	    }
	 
	// ------------------------ INTERFACE METHODS ------------------------
	 
	 
	// --------------------- Interface Conversation ---------------------
	 
	    public void begin()
	    {
	        verifyConversationContextActive();
	        if (!_transient) {
	            throw new IllegalStateException("BEGIN_CALLED_ON_LONG_RUNNING_CONVERSATION");
	        }
	        _transient = false;
	        if (this.id == null) {
	            // This a conversation that was made transient previously in this request
	            this.id = "" + CONVERSATION_ID_COUNTER++;
	            ACTIVE_CONVERSATIONS.add(this.id);
	        }
	    }
	 
	    public void begin(String id)
	    {
	        verifyConversationContextActive();
	        if (!_transient) {
	            throw new IllegalStateException("BEGIN_CALLED_ON_LONG_RUNNING_CONVERSATION");
	        }
	        if (ACTIVE_CONVERSATIONS.contains(id)) {
	            throw new IllegalStateException("CONVERSATION_ID_ALREADY_IN_USE:" + id);
	        }
	        _transient = false;
	        this.id = id;
	    }
	 
	    public void end()
	    {
	        if (_transient) {
	            throw new IllegalStateException("END_CALLED_ON_TRANSIENT_CONVERSATION");
	        }
	        _transient = true;
	        ACTIVE_CONVERSATIONS.remove(this.id);
	    }
	 
	    public String getId()
	    {
	        verifyConversationContextActive();
	        if (!_transient) {
	            return id;
	        } else {
	            return null;
	        }
	    }
	 
	    public boolean isTransient()
	    {
	        verifyConversationContextActive();
	        return _transient;
	    }
	 
	    @PreDestroy
	    private void onDestroy()
	    {
	        ACTIVE_CONVERSATIONS.remove(this.id);
	    }
	 
	    private void verifyConversationContextActive()
	    {
	        try {
	            beanManager.getContext(ConversationScoped.class);
	        } catch (ContextNotActiveException e) {
	            throw new ContextNotActiveException("Conversation Context not active when method called on conversation " + this, e);
	        }
	    }

		@Override
		public long getLastUsed() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean lock(long arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void touch() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean unlock() {
			// TODO Auto-generated method stub
			return false;
		}

}
