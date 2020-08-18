// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/* Creates a DOM element with specified type, className, and optionally parent and innerText */
const create = (elementType, elementClassName, elementParent, elementInnerText) => {
  const element = document.createElement(elementType);
  element.className = elementClassName;

  if(elementInnerText !== undefined) element.innerText = elementInnerText;
  if(elementParent !== undefined) elementParent.appendChild(element);
  
  return element;
}

const addCommentElement = (comment) => {
  const commentElement = create('div', 'comment');
  create('p', 'nickname', commentElement, comment.nickname);
  create('p', 'commentbox', commentElement, comment.comment);

  return commentElement;
}

const getComments = () => {
  fetch('/data')
  .then(data => data.json())
  .then(comments => {
    console.log(comments);
    const container = document.getElementById('comments-container');
    container.innerHTML = ''; // First Clear Children
    comments.forEach(comment => container.appendChild(addCommentElement(comment)));
  });
};

const initialiseCommentRefreshButton = () => document.getElementById('comments-refresh').onclick = getComments;

const navigate = (pageId) => document.getElementById(pageId).scrollIntoView({behavior: 'smooth', block: 'start'});

const initialiseNavigation = () => {
  const navigationItems = document.getElementsByClassName('navigation-item');
  for (const item of navigationItems) {
    item.addEventListener('click', function() {
      navigate(this.getAttribute('data-link'));
    })
  };
  
  const sectionSelector = document.getElementById('section-selector');
  sectionSelector.addEventListener('change', function (e) {
    navigate(e.target.value);
  });

  document.body.onscroll = onscroll;
};

const onscroll = () => {
  const container = document.documentElement;
  const scrollHeight = container.scrollHeight - container.clientHeight;
  const scrollTop = container.scrollTop;
  const PROMPT_SECTION = 0;
  const COMMENT_SECTION = 5;
  const NUMBER_OF_SECTIONS = 5;

  const percentage = Math.floor(scrollTop / scrollHeight * 100);
  const section = Math.floor(percentage / (100 / NUMBER_OF_SECTIONS));

  // section == COMMENT_SECTION represents comment section, which is not on the nav.
  const sectionSelector = document.getElementById('section-selector');
  if (section == COMMENT_SECTION) {
    sectionSelector.selectedIndex = PROMPT_SECTION; // PROMPT_SECTION is the "Jump to..." prompt.
  }
  else {
    sectionSelector.selectedIndex = section;
  }

}

const onload = () => {
  new Promise((resolve, reject) => {
    getComments();
    resolve(true);
    reject(false);
  })
  .then(_ => initialiseNavigation())
  .then(_ => initialiseCommentRefreshButton());
};

window.onload = () => onload();
