package com.xxx.animal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.animal.entity.DonationClaim;
import com.xxx.animal.mapper.DonationClaimMapper;
import com.xxx.animal.service.DonationClaimService;
import org.springframework.stereotype.Service;

@Service
public class DonationClaimServiceImpl extends ServiceImpl<DonationClaimMapper, DonationClaim> implements DonationClaimService {
}
