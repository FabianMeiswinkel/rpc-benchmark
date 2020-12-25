﻿//------------------------------------------------------------
// Copyright (c) Microsoft Corporation.  All rights reserved.
//------------------------------------------------------------
namespace Microsoft.Azure.Documents
{
    using Microsoft.Azure.Documents.Collections;
    using System.Threading.Tasks;

    internal interface IAuthorizationTokenProvider
    {
        // ValueTask<(string token, string payload)> GetUserAuthorizationAsync(
        string GetUserAuthorizationToken(
            string resourceAddress,
            string resourceType,
            string requestVerb,
            INameValueCollection headers,
            AuthorizationTokenType tokenType,
            out string payload);

        Task AddSystemAuthorizationHeaderAsync(
            DocumentServiceRequest request,
            string federationId,
            string verb,
            string resourceId);
    }
}
