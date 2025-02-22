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
package com.axelor.apps.base.web;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.ResponseMessageType;
import com.axelor.apps.base.db.Localization;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.base.service.localization.LocalizationService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class LocalizationController {
  public void validateLocale(ActionRequest request, ActionResponse response) {
    try {
      Localization localization = request.getContext().asType(Localization.class);
      Beans.get(LocalizationService.class).validateLocale(localization);
    } catch (Exception e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
    }
  }

  public void computeDateAndNumbersFormatPattern(ActionRequest request, ActionResponse response) {
    Localization localization = request.getContext().asType(Localization.class);
    // validate locale first
    if (localization.getCountry() == null || localization.getLanguage() == null) {
      return;
    }
    LocalizationService localizationService = Beans.get(LocalizationService.class);
    try {
      localizationService.validateLocale(localization);
    } catch (AxelorException e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
      return;
    }
    String numbersFormat = localizationService.getNumberFormat(localization.getCode());
    String dateFormat = localizationService.getDateFormat(localization.getCode());
    response.setValue("dateFormat", dateFormat);
    response.setValue("numbersFormat", numbersFormat);
  }
}
