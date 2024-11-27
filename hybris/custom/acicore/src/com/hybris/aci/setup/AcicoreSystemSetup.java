/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.setup;

import static com.hybris.aci.constants.AcicoreConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.hybris.aci.constants.AcicoreConstants;
import com.hybris.aci.service.AcicoreService;


@SystemSetup(extension = AcicoreConstants.EXTENSIONNAME)
public class AcicoreSystemSetup
{
	private final AcicoreService acicoreService;

	public AcicoreSystemSetup(final AcicoreService acicoreService)
	{
		this.acicoreService = acicoreService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		acicoreService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return AcicoreSystemSetup.class.getResourceAsStream("/acicore/sap-hybris-platform.png");
	}
}
