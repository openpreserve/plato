package eu.scape_project.planning.plato.mock;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.ManagedConversation;

public class MockConversationContext implements ConversationContext{

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T get(Contextual<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(Contextual<T> arg0, CreationalContext<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return ConversationScoped.class;
		
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateConversationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getConcurrentAccessTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ManagedConversation getConversation(String arg0) {
		return new MockConversation();
	}

	@Override
	public Collection<ManagedConversation> getConversations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ManagedConversation getCurrentConversation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDefaultTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getParameterName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConcurrentAccessTimeout(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultTimeout(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameterName(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
