package com.axelor.apps.sale.service.saleorder;

import com.axelor.apps.sale.db.SaleOrderLine;

public interface SaleOrderLineMultipleQtyService {

  String getMultipleQtyErrorMessage(SaleOrderLine saleOrderLine);
}
