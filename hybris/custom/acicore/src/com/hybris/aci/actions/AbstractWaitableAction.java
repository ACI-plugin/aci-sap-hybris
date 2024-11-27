/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.actions;

import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.HashSet;
import java.util.Set;


/**
 * extends AbstractAction with three states (OK, NOK, WAIT)
 *
 * @param <T>
 */
abstract public class AbstractWaitableAction<T extends BusinessProcessModel> extends AbstractAction<T>
{
	public enum Transition
	{
		AUTHOK, AUTHNOK, AUTHWAIT, SALEOK, SALENOK, ;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}
}
