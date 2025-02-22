/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.businessproject.service.app;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.PrintingTemplate;
import com.axelor.apps.base.db.Unit;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.TraceBackRepository;
import com.axelor.apps.base.exceptions.BaseExceptionMessage;
import com.axelor.apps.base.service.app.AppBaseServiceImpl;
import com.axelor.apps.businessproject.db.BusinessProjectConfig;
import com.axelor.apps.businessproject.db.repo.BusinessProjectConfigRepository;
import com.axelor.apps.businessproject.exception.BusinessProjectExceptionMessage;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.meta.db.repo.MetaModuleRepository;
import com.axelor.studio.app.service.AppVersionService;
import com.axelor.studio.db.AppBusinessProject;
import com.axelor.studio.db.repo.AppBusinessProjectRepository;
import com.axelor.studio.db.repo.AppRepository;
import com.axelor.studio.service.AppSettingsStudioService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Singleton
public class AppBusinessProjectServiceImpl extends AppBaseServiceImpl
    implements AppBusinessProjectService {

  protected AppBusinessProjectRepository appBusinessProjectRepo;
  protected BusinessProjectConfigRepository businessProjectConfigRepository;
  protected CompanyRepository companyRepository;

  @Inject
  public AppBusinessProjectServiceImpl(
      AppRepository appRepo,
      MetaFiles metaFiles,
      AppVersionService appVersionService,
      MetaModelRepository metaModelRepo,
      AppSettingsStudioService appSettingsService,
      MetaModuleRepository metaModuleRepo,
      MetaFileRepository metaFileRepo,
      AppBusinessProjectRepository appBusinessProjectRepo,
      BusinessProjectConfigRepository businessProjectConfigRepository,
      CompanyRepository companyRepository) {
    super(
        appRepo,
        metaFiles,
        appVersionService,
        metaModelRepo,
        appSettingsService,
        metaModuleRepo,
        metaFileRepo);
    this.appBusinessProjectRepo = appBusinessProjectRepo;
    this.businessProjectConfigRepository = businessProjectConfigRepository;
    this.companyRepository = companyRepository;
  }

  @Override
  public AppBusinessProject getAppBusinessProject() {
    return appBusinessProjectRepo.all().fetchOne();
  }

  @Override
  public Unit getDaysUnit() throws AxelorException {
    Unit daysUnit = getAppBusinessProject().getDaysUnit();
    if (Objects.isNull(daysUnit)) {
      throw new AxelorException(
          getAppBusinessProject(),
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(BusinessProjectExceptionMessage.PROJECT_CONFIG_DAYS_UNIT_MISSING));
    }
    return daysUnit;
  }

  @Override
  public Unit getHoursUnit() throws AxelorException {
    Unit hoursUnit = getAppBusinessProject().getHoursUnit();
    if (Objects.isNull(hoursUnit)) {
      throw new AxelorException(
          getAppBusinessProject(),
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(BusinessProjectExceptionMessage.PROJECT_CONFIG_HOURS_UNIT_MISSING));
    }
    return hoursUnit;
  }

  @Override
  public BigDecimal getDefaultHoursADay() throws AxelorException {
    BigDecimal hoursUnit = getAppBusinessProject().getDefaultHoursADay();
    if (Objects.isNull(hoursUnit) || hoursUnit.signum() <= 0) {
      throw new AxelorException(
          getAppBusinessProject(),
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(BusinessProjectExceptionMessage.PROJECT_CONFIG_DEFAULT_HOURS_PER_DAY_MISSING));
    }
    return hoursUnit;
  }

  @Override
  public PrintingTemplate getInvoicingAnnexPrintTemplate() throws AxelorException {
    PrintingTemplate invoicingAnnexPrintTemplate =
        getAppBusinessProject().getInvoicingAnnexPrintTemplate();
    if (invoicingAnnexPrintTemplate == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(BaseExceptionMessage.TEMPLATE_CONFIG_NOT_FOUND));
    }
    return invoicingAnnexPrintTemplate;
  }

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public void generateBusinessProjectConfigurations() {

    List<Company> companies =
        companyRepository.all().filter("self.businessProjectConfig is null").fetch();

    for (Company company : companies) {
      BusinessProjectConfig businessProjectConfig = new BusinessProjectConfig();
      businessProjectConfig.setCompany(company);
      businessProjectConfigRepository.save(businessProjectConfig);
    }
  }
}
