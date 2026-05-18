import { backendUrl } from 'src/boot/axios'

export const oauthApi = {
  loginWithGithub: () => {
    window.location.href = `${backendUrl}/oauth2/authorization/github`
  },
}
