import { createStore } from "vuex"

import homeStore from '@/store/home/home.store'

const store = createStore(homeStore)

export default store