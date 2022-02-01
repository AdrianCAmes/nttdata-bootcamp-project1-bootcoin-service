package com.nttdata.bootcamp.bootcoinservice.utils.impl;

import com.nttdata.bootcamp.bootcoinservice.model.BootcoinWallet;
import com.nttdata.bootcamp.bootcoinservice.model.dto.request.BootcoinWalletCreateRequestDTO;
import com.nttdata.bootcamp.bootcoinservice.utils.BootcoinWalletUtils;
import org.springframework.stereotype.Component;

@Component
public class BootcoinWalletUtilsimpl implements BootcoinWalletUtils {
    @Override
    public BootcoinWallet bootcoinWalletCreateRequestDTOToBootcoinWallet(BootcoinWalletCreateRequestDTO walletDTO) {
        return BootcoinWallet.builder()
                .customer(walletDTO.getCustomer())
                .build();
    }

    @Override
    public BootcoinWalletCreateRequestDTO bootcoinWalletToBootcoinWalletCreateRequestDTO(BootcoinWallet wallet) {
        return BootcoinWalletCreateRequestDTO.builder()
                .customer(wallet.getCustomer())
                .build();
    }
}
