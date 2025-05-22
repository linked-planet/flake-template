/*-
 * #%L
 * jmenu
 * %%
 * Copyright (C) 2025 linked-planet GmbH
 * %%
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
 * #L%
 */

const MENU_ROOT_ID = 'jmenu.menu-item-content'

/**
 * A child entry of the menu.
 *
 * @typedef {Object} ServicesMenuConfigEntryChild
 * @property {string} title
 * @property {string} link
 * @property {string} target
 */

/**
 * A parent entry of the menu.
 *
 * @typedef {Object} ServicesMenuConfigEntryParent
 * @property {string} title
 * @property {string | undefined} link
 * @property {string} target
 * @property {ServicesMenuConfigEntryChild[] | undefined} children
 */

/**
 * Fetches the menu entries.
 *
 * @returns {Promise<ServicesMenuConfigEntryParent[]>}
 */
async function fetchServicesMenuEntries() {
    const response = await fetch('/rest/jmenu/1.0/config');
    if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
    }
    return await response.json();
}

/**
 * Adds a single element to the menu.
 *
 * @param {HTMLElement} menuElement
 * @param {ServicesMenuConfigEntryParent} entry
 * @param {Boolean} first
 */
function addServiceMenuEntry(menuElement, entry, first) {
    if (entry.children && entry.children.length > 0) {
        const entryElement = document.createElement('li')
        entryElement.style.borderTop = !first ? '1px solid var(--aui-dropdown-border-color)' : 'none'
        entryElement.style.paddingTop = !first ? '3px' : 0
        entryElement.style.marginTop = !first ? '3px' : 0
        entryElement.classList.add('aui-dropdown2-section')
        entryElement.innerHTML = `<strong>${entry.title}</strong>`
        menuElement.appendChild(entryElement)

        entry.children.forEach(child => {
            addServiceMenuEntry(menuElement, child, false)
        })
    } else if (entry.link) {
        const entryElement = document.createElement('li')
        entryElement.innerHTML = `<a target='${entry.target}' href='${entry.link}'>${entry.title}</a>`
        menuElement.appendChild(entryElement)
    }
}

/**
 *  Initializes the menu in the DOM.
 *
 * @param {ServicesMenuConfigEntryParent[]} entries
 */
function initializeServicesMenu(entries) {
    const menuElement = document.getElementById(MENU_ROOT_ID)

    const menuListElement = document.createElement('ul')
    menuListElement.classList.add('aui-list-truncate')
    menuElement.appendChild(menuListElement)

    entries.forEach((entry, index) => {
        addServiceMenuEntry(menuListElement, entry, index === 0)
    })
}


AJS.toInit(async function () {
    try {
        const menu = await fetchServicesMenuEntries();
        initializeServicesMenu(menu);
    } catch (error) {
        console.error('Error generating services menu:', error);
        return null;
    }
})
