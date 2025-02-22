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
package com.axelor.apps.bankpayment.service.bankstatementline.afb120;

import com.axelor.apps.bankpayment.db.BankStatementLineAFB120;
import com.axelor.apps.bankpayment.db.repo.BankPaymentBankStatementLineAFB120Repository;
import com.axelor.apps.bankpayment.db.repo.BankStatementLineAFB120Repository;
import com.axelor.apps.bankpayment.service.config.BankPaymentConfigService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.PrintingTemplate;
import com.axelor.apps.base.service.printing.template.PrintingTemplatePrintService;
import com.axelor.apps.base.service.printing.template.model.PrintingGenFactoryContext;
import com.axelor.common.ObjectUtils;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.Map;

public class BankStatementLinePrintAFB120ServiceImpl
    implements BankStatementLinePrintAFB120Service {

  protected BankPaymentBankStatementLineAFB120Repository
      bankPaymentBankStatementLineAFB120Repository;
  protected BankPaymentConfigService bankPaymentConfigService;
  protected PrintingTemplatePrintService printingTemplatePrintService;

  @Inject
  public BankStatementLinePrintAFB120ServiceImpl(
      BankPaymentBankStatementLineAFB120Repository bankPaymentBankStatementLineAFB120Repository,
      BankPaymentConfigService bankPaymentConfigService,
      PrintingTemplatePrintService printingTemplatePrintService) {
    this.bankPaymentBankStatementLineAFB120Repository =
        bankPaymentBankStatementLineAFB120Repository;
    this.bankPaymentConfigService = bankPaymentConfigService;
    this.printingTemplatePrintService = printingTemplatePrintService;
  }

  @Override
  public String print(
      LocalDate fromDate,
      LocalDate toDate,
      BankDetails bankDetails,
      PrintingTemplate bankStatementLinesPrintTemplate)
      throws AxelorException {
    String fileLink = null;

    BankStatementLineAFB120 initalBankStatementLine =
        bankPaymentBankStatementLineAFB120Repository.findLineBetweenDate(
            fromDate,
            toDate,
            BankStatementLineAFB120Repository.LINE_TYPE_INITIAL_BALANCE,
            true,
            bankDetails);
    BankStatementLineAFB120 finalBankStatementLine =
        bankPaymentBankStatementLineAFB120Repository.findLineBetweenDate(
            fromDate,
            toDate,
            BankStatementLineAFB120Repository.LINE_TYPE_FINAL_BALANCE,
            false,
            bankDetails);
    if (ObjectUtils.notEmpty(initalBankStatementLine)
        && ObjectUtils.notEmpty(finalBankStatementLine)) {
      if (bankStatementLinesPrintTemplate == null) {
        bankStatementLinesPrintTemplate =
            bankPaymentConfigService.getBankStatementLinePrintTemplate(bankDetails.getCompany());
      }
      fromDate = initalBankStatementLine.getOperationDate();
      toDate = finalBankStatementLine.getOperationDate();

      fileLink =
          printingTemplatePrintService.getPrintLink(
              bankStatementLinesPrintTemplate,
              new PrintingGenFactoryContext(
                  Map.of(
                      "FromDate", fromDate, "ToDate", toDate, "BankDetails", bankDetails.getId())),
              "Bank statement lines - " + fromDate + " to " + toDate);
    }
    return fileLink;
  }
}
