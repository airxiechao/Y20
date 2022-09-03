const loginMutations = {
  setTwoFactorToken(state, { twoFactorToken }){
    state.twoFactorToken = twoFactorToken
  },
}

export default loginMutations