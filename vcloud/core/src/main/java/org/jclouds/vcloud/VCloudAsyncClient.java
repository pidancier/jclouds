/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud;

import static org.jclouds.vcloud.VCloudMediaType.TASK_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPPTEMPLATE_XML;
import static org.jclouds.vcloud.VCloudMediaType.VAPP_XML;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.predicates.validators.DnsNameValidator;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.binders.BindCloneVAppParamsToXmlPayload;
import org.jclouds.vcloud.binders.BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.functions.OrgNameCatalogNameVAppTemplateNameToEndpoint;
import org.jclouds.vcloud.functions.OrgNameVDCNameResourceEntityNameToEndpoint;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface VCloudAsyncClient extends CommonVCloudAsyncClient {

   /**
    * @see VCloudClient#getVAppTemplate
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> getVAppTemplate(@EndpointParam URI vAppTemplate);

   /**
    * @see VCloudClient#findVAppTemplateInOrgCatalogNamed
    */
   @GET
   @Consumes(VAPPTEMPLATE_XML)
   @XMLResponseParser(VAppTemplateHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VAppTemplate> findVAppTemplateInOrgCatalogNamed(
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameCatalogNameVAppTemplateNameToEndpoint.class) String itemName);

   /**
    * @see VCloudClient#instantiateVAppTemplateInVDC
    */
   @POST
   @Path("action/instantiateVAppTemplate")
   @Produces("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   // TODO convert this.
   @MapBinder(BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload.class)
   ListenableFuture<? extends VApp> instantiateVAppTemplateInVDC(@EndpointParam URI vdc,
            @MapPayloadParam("template") URI template,
            @MapPayloadParam("name") @ParamValidators(DnsNameValidator.class) String appName,
            InstantiateVAppTemplateOptions... options);

   /**
    * @see VCloudClient#cloneVAppInVDC
    */
   @POST
   @Path("/action/cloneVApp")
   @Produces("application/vnd.vmware.vcloud.cloneVAppParams+xml")
   @Consumes(TASK_XML)
   @XMLResponseParser(TaskHandler.class)
   @MapBinder(BindCloneVAppParamsToXmlPayload.class)
   ListenableFuture<? extends Task> cloneVAppInVDC(@EndpointParam URI vdc, @MapPayloadParam("vApp") URI toClone,
            @MapPayloadParam("newName") @ParamValidators(DnsNameValidator.class) String newName,
            CloneVAppOptions... options);

   /**
    * @see VCloudClient#findVAppInOrgVDCNamed
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> findVAppInOrgVDCNamed(
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String orgName,
            @Nullable @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String catalogName,
            @EndpointParam(parser = OrgNameVDCNameResourceEntityNameToEndpoint.class) String vAppName);

   /**
    * @see VCloudClient#getVApp
    */
   @GET
   @Consumes(VAPP_XML)
   @XMLResponseParser(VAppHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<? extends VApp> getVApp(@EndpointParam URI vApp);
}
