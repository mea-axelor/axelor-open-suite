/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.base.web;

import com.axelor.apps.account.db.PaymentMode;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.service.BankDetailsServiceImpl;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

/**
 * Controller called from multiple forms,
 */
public class CompanyBankDetailsController {

    /**
     * Set the domain of company bank details field
     *
     * @param request
     * @param response
     */
    public void fillCompanyBankDetailsDomain(ActionRequest request, ActionResponse response) {
        Company company = (Company) request.getContext().get("company");
        PaymentMode paymentMode = (PaymentMode) request.getContext().get("paymentMode");
        response.setAttr(
                "companyBankDetails",
                "domain",
                Beans.get(BankDetailsServiceImpl.class)
                        .createCompanyBankDetailsDomain(company, paymentMode)
        );
    }
}
