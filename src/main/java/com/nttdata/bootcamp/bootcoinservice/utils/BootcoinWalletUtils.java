package com.nttdata.bootcamp.bootcoinservice.utils;

import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletCreateRequestDTO;

public interface BootcoinWalletUtils {
    BootcoinWallet bootcoinWalletCreateRequestDTOToBootcoinWallet(BootcoinWalletCreateRequestDTO walletDTO);
    BootcoinWalletCreateRequestDTO bootcoinWalletToBootcoinWalletCreateRequestDTO(BootcoinWallet wallet);
}
