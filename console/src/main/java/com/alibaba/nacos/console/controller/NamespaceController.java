package com.alibaba.nacos.console.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.nacos.config.server.exception.NacosException;
import com.alibaba.nacos.config.server.model.RestResult;
import com.alibaba.nacos.config.server.model.TenantInfo;
import com.alibaba.nacos.config.server.service.PersistService;
import com.alibaba.nacos.config.server.utils.StringUtils;
import com.alibaba.nacos.console.model.Namespace;
import com.alibaba.nacos.console.model.NamespaceAllInfo;

/**
 * namespace service
 * 
 * @author Nacos
 *
 */
@Controller
@RequestMapping("/v1/console/namespaces")
public class NamespaceController {

	@Autowired
	private transient PersistService persistService;

	/**
	 * Get namespace list
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @return namespace list
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public RestResult<List<Namespace>> getNamespaces(HttpServletRequest request, HttpServletResponse response) {
		RestResult<List<Namespace>> rr = new RestResult<List<Namespace>>();
		rr.setCode(200);
		// TODO 获取用kp
		List<TenantInfo> tenantInfos = persistService.findTenantByKp("1");
		Namespace namespace0 = new Namespace("", "Public", 200, persistService.configInfoCount(""), 0);
		List<Namespace> namespaces = new ArrayList<Namespace>();
		namespaces.add(namespace0);
		for (TenantInfo tenantInfo : tenantInfos) {
			int configCount = persistService.configInfoCount(tenantInfo.getTenantId());
			Namespace namespaceTmp = new Namespace(tenantInfo.getTenantId(), tenantInfo.getTenantName(), 200,
					configCount, 2);
			namespaces.add(namespaceTmp);
		}
		rr.setData(namespaces);
		return rr;
	}

	/**
	 * get namespace all info by namespace id
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param namespaceId
	 *            namespaceId
	 * @return namespace all info
	 */
	@ResponseBody
	@RequestMapping(params = "show=all", method = RequestMethod.GET)
	public NamespaceAllInfo getNamespace(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("namespaceId") String namespaceId) {
		// TODO 获取用kp
		if (StringUtils.isBlank(namespaceId)) {
			int configCount = persistService.configInfoCount();
			NamespaceAllInfo namespaceTmp = new NamespaceAllInfo(namespaceId, "Public", 200, configCount, 0,
					"Public Namespace");
			return namespaceTmp;
		} else {
			TenantInfo tenantInfo = persistService.findTenantByKp("1", namespaceId);
			int configCount = persistService.configInfoCount(namespaceId);
			NamespaceAllInfo namespaceTmp = new NamespaceAllInfo(namespaceId, tenantInfo.getTenantName(), 200,
					configCount, 2, tenantInfo.getTenantDesc());
			return namespaceTmp;
		}
	}

	/**
	 * create namespace
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param namespaceName
	 *            namespace Name
	 * @param namespaceDesc
	 *            namespace Desc
	 * @return whether create ok
	 * @throws NacosException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Boolean createNamespace(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("namespaceName") String namespaceName,
			@RequestParam(value = "namespaceDesc", required = false) String namespaceDesc) throws NacosException {
		// TODO 获取用kp
		String namespaceId = UUID.randomUUID().toString();
		persistService.insertTenantInfoAtomic("1", namespaceId, namespaceName, namespaceDesc, "nacos",
				System.currentTimeMillis());
		return true;
	}

	/**
	 * edit namespace
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param namespace
	 *            namespace
	 * @param namespaceShowName
	 *            namespace ShowName
	 * @param namespaceDesc
	 *            namespace Desc
	 * @return whether edit ok
	 * @throws NacosException
	 *             NacosException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public Boolean editNamespace(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("namespace") String namespace, @RequestParam("namespaceShowName") String namespaceShowName,
			@RequestParam(value = "namespaceDesc", required = false) String namespaceDesc) throws NacosException {
		// TODO 获取用kp
		persistService.updateTenantNameAtomic("1", namespace, namespaceShowName, namespaceDesc);
		return true;
	}

	/**
	 * del namespace by id
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param namespaceId
	 *            namespace Id
	 * @return whether del ok
	 * @throws NacosException
	 *             NacosException
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public Boolean deleteConfig(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("namespaceId") String namespaceId) throws NacosException {
		persistService.removeTenantInfoAtomic("1", namespaceId);
		return true;
	}

}
