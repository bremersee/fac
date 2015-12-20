/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.fac.example.web;

import java.util.Arrays;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bremersee.comparator.ComparatorItemTransformer;
import org.bremersee.comparator.model.ComparatorItem;
import org.bremersee.fac.FailedAccessCounter;
import org.bremersee.fac.model.AccessResultDto;
import org.bremersee.pagebuilder.PageControlFactory;
import org.bremersee.pagebuilder.model.PageControlDto;
import org.bremersee.pagebuilder.model.PageDto;
import org.bremersee.pagebuilder.model.PageRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Christian Bremer
 */
@Controller
public class FacController {

    public static final String SUCCESS_MESSAGES_KEY = "SUCCESS_MESSAGES";

    private static int resourceCounter = 0;

    protected static synchronized String getNextResourceName() {
        resourceCounter = resourceCounter + 1;
        return "resource_" + resourceCounter;
    }

    @Inject
    protected FailedAccessCounter failedAccessCounter;

    @Inject
    protected ComparatorItemTransformer comparatorItemTransformer;

    // no equivalent for @Inject
    @Autowired(required = false)
    protected LocaleResolver localeResolver;

    protected PageControlFactory pageControlFactory;

    @PostConstruct
    public void init() {
        //@formatter:off
        pageControlFactory = PageControlFactory.newInstance()
                .setComparatorItemTransformer(comparatorItemTransformer)
                .setComparatorParamName("c")
                .setMaxPaginationLinks(7)
                .setPageNumberParamName("p")
                .setPageSizeParamName("s")
                .setPageSizeSelectorMaxValue(40)
                .setPageSizeSelectorMinValue(10)
                .setPageSizeSelectorStep(10)
                .setQueryParamName("q")
                .setQuerySupported(true)
                .setSelectAllEntriesAvailable(true);
        //@formatter:on
    }

    protected Locale resolveLocale(HttpServletRequest request) {
        Locale locale;
        if (localeResolver != null) {
            locale = localeResolver.resolveLocale(request);
        } else {
            locale = request.getLocale();
        }
        if (locale == null || StringUtils.isBlank(locale.getLanguage())) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    protected void addSuccessMessages(RedirectAttributes redirectAttrs, String... messages) {

        if (redirectAttrs != null && messages != null && messages.length > 0) {
            if (messages.length == 1) {
                redirectAttrs.addFlashAttribute(SUCCESS_MESSAGES_KEY, messages[0]);
            } else {
                redirectAttrs.addFlashAttribute(SUCCESS_MESSAGES_KEY, Arrays.asList(messages));
            }
        }
    }

    @RequestMapping(value = "/entries.html", method = RequestMethod.GET)
    public String displayFailedAccessEntries(HttpServletRequest request,
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int pageSize,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "resourceId,asc|remoteHost,asc") String comparator, Model model) {

        ComparatorItem comparatorItem = comparatorItemTransformer.fromString(comparator, false, null);

        PageRequestDto pageRequest = new PageRequestDto(pageNumber, pageSize, comparatorItem, query, null);

        PageDto pageDto = failedAccessCounter.getFailedAccessEntries(pageRequest);

        PageControlDto pageControl = pageControlFactory.newPageControl(pageDto, "entries.html", resolveLocale(request));

        model.addAttribute("pageControl", pageControl);

        model.addAttribute("counterThreshold", failedAccessCounter.getFailedAccessCounterThreshold());

        model.addAttribute("removeFailedAccessEntriesAfterMillis",
                failedAccessCounter.getRemoveFailedAccessEntriesAfterMillis());
        model.addAttribute("removeFailedEntriesInterval", failedAccessCounter.getRemoveFailedEntriesInterval());

        model.addAttribute("lastRemovingOfFailedEntries", failedAccessCounter.getLastRemovingOfFailedEntries());
        model.addAttribute("lastRemovingOfFailedEntriesDuration",
                failedAccessCounter.getLastRemovingOfFailedEntriesDuration());
        model.addAttribute("lastRemovingOfFailedEntriesSize", failedAccessCounter.getLastRemovingOfFailedEntriesSize());

        model.addAttribute("removedFailedEntriesTotalSize", failedAccessCounter.getRemovedFailedEntriesTotalSize());

        return "entries";
    }

    @RequestMapping(value = "/accessFailed.html", method = RequestMethod.GET)
    public String accessFailed(HttpServletRequest request, @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int pageSize,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "") String comparator,
            @RequestParam(value = "resourceId", defaultValue = "") String resourceId, Model model,
            RedirectAttributes redirectAttrs) {

        String remoteHost = request.getRemoteHost();
        if (StringUtils.isBlank(remoteHost)) {
            remoteHost = "_unknown_";
        }

        if (StringUtils.isBlank(resourceId)) {
            resourceId = getNextResourceName();
        }

        failedAccessCounter.accessFailed(resourceId, remoteHost, System.currentTimeMillis());

        redirectAttrs.addAttribute("c", comparator);
        redirectAttrs.addAttribute("q", query);
        redirectAttrs.addAttribute("s", pageSize);
        redirectAttrs.addAttribute("p", pageNumber);

        return "redirect:entries.html";
    }

    @RequestMapping(value = "/accessSucceeded.html", method = RequestMethod.GET)
    public String accessSucceeded(HttpServletRequest request,
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int pageSize,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "") String comparator,
            @RequestParam(value = "resourceId", defaultValue = "") String resourceId, Model model,
            RedirectAttributes redirectAttrs) {

        String remoteHost = request.getRemoteHost();
        if (StringUtils.isBlank(remoteHost)) {
            remoteHost = "_unknown_";
        }

        if (StringUtils.isNotBlank(resourceId)) {
            failedAccessCounter.accessSucceeded(resourceId, remoteHost, System.currentTimeMillis());
        }

        redirectAttrs.addAttribute("c", comparator);
        redirectAttrs.addAttribute("q", query);
        redirectAttrs.addAttribute("s", pageSize);
        redirectAttrs.addAttribute("p", pageNumber);

        return "redirect:entries.html";
    }

    @RequestMapping(value = "/remove.html", method = RequestMethod.GET)
    public String removeAccessFailedEntry(HttpServletRequest request,
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int pageSize,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "") String comparator,
            @RequestParam(value = "resourceId", defaultValue = "") String resourceId, Model model,
            RedirectAttributes redirectAttrs) {

        String remoteHost = request.getRemoteHost();
        if (StringUtils.isBlank(remoteHost)) {
            remoteHost = "_unknown_";
        }

        if (StringUtils.isNotBlank(resourceId)) {
            failedAccessCounter.removeFailedAccessEntry(resourceId, remoteHost);
        }

        redirectAttrs.addAttribute("c", comparator);
        redirectAttrs.addAttribute("q", query);
        redirectAttrs.addAttribute("s", pageSize);
        redirectAttrs.addAttribute("p", pageNumber);

        return "redirect:entries.html";
    }

    @RequestMapping(value = "/removeObsolete.html", method = RequestMethod.GET)
    public String removeObsoleteAccessFailedEntries(HttpServletRequest request,
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int pageSize,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "") String comparator, Model model,
            RedirectAttributes redirectAttrs) {

        failedAccessCounter.removeObsoleteFailedAccessEntries();

        redirectAttrs.addAttribute("c", comparator);
        redirectAttrs.addAttribute("q", query);
        redirectAttrs.addAttribute("s", pageSize);
        redirectAttrs.addAttribute("p", pageNumber);

        return "redirect:entries.html";
    }

    @RequestMapping(value = "/resource.html", method = RequestMethod.GET)
    public String displayResource(HttpServletRequest request,
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "s", defaultValue = "20") int maxResults,
            @RequestParam(value = "p", defaultValue = "0") int pageNumber,
            @RequestParam(value = "c", defaultValue = "") String comparator,
            @RequestParam(value = "resourceId", defaultValue = "") String resourceId, Model model,
            RedirectAttributes redirectAttrs) {

        if (StringUtils.isBlank(resourceId)) {
            return "redirect:entries.html";
        }

        String remoteHost = request.getRemoteHost();
        if (StringUtils.isBlank(remoteHost)) {
            remoteHost = "_unknown_";
        }

        AccessResultDto result = failedAccessCounter.isAccessGranted(resourceId, remoteHost);

        model.addAttribute("resourceId", resourceId);
        model.addAttribute("result", result);

        model.addAttribute("c", comparator);
        model.addAttribute("q", query);
        model.addAttribute("s", maxResults);
        model.addAttribute("p", pageNumber);

        return "resource";
    }

}
